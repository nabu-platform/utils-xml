/*
* Copyright (C) 2015 Alexander Verbruggen
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package be.nabu.utils.xml.diff;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="definition")
public class DiffKeyCollection {
	private List<DiffKey> keys;

	@XmlElement(name = "record")
	public List<DiffKey> getKeys() {
		return keys;
	}

	public void setKeys(List<DiffKey> keys) {
		this.keys = keys;
	}
	
	public static DiffKeyCollection unmarshal(InputStream input) {
		try {
			JAXBContext context = JAXBContext.newInstance(DiffKeyCollection.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			DiffKeyCollection collection = (DiffKeyCollection) unmarshaller.unmarshal(input);
			for (DiffKey key : collection.getKeys())
				resolveReferences(key, collection);
			return collection;
		}
		catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
	public static DiffKeyCollection unmarshal(File file) {
		try {
			JAXBContext context = JAXBContext.newInstance(DiffKeyCollection.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			DiffKeyCollection collection = (DiffKeyCollection) unmarshaller.unmarshal(file);
			for (DiffKey key : collection.getKeys())
				resolveReferences(key, collection);
			return collection;
		}
		catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static void resolveReferences(DiffKey key, DiffKeyCollection collection) {
		// first recursively resolve any children
		for (DiffKey child : key.getChildren())
			resolveReferences(child, collection);
				
		// once the children are done, add the references
		for (DiffKeyReference reference : key.getChildReferences()) {
			for (DiffKey rootKey : collection.getKeys()) {
				if (reference.getReferencePath().equals("*") || rootKey.getPath().equals(reference.getReferencePath())) {
					DiffKey newKey = new DiffKey();
					newKey.setPath(reference.getPath() == null ? reference.getReferencePath() : reference.getPath());
					newKey.setChildren(rootKey.getChildren());
					newKey.setFields(rootKey.getFields());
					newKey.setKeys(rootKey.getKeys());
					key.getChildren().add(newKey);
//					System.out.println("Adding child " + newKey.getPath() + " to " + key.getPath());
				}
			}
		}
				
	}
}
