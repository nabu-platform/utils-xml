This project combines some XML-based utilities:

- Basic utilities to create documents, schemes, validate, transform,...
- Basic implementations of resource resolvers, optionally contextually aware namespace resolvers,...
- an XPath wrapper that allows for method chaining
- XML Diffing logic based on an API that has two diff algorithm implementations:
	- Simple diff: node for node diffing
	- Structured diff: given a definition, can diff more intelligently (you can define keys & values)
- The XML Diffing can be visualized as a unified diff derivative or can be merged into a single file with changes annotated in attributes