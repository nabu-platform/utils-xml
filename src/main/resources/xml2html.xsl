<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:o="urn:oasis:names:tc:opendocument:xmlns:office:1.0" xmlns:t="urn:oasis:names:tc:opendocument:xmlns:text:1.0" xmlns:d="urn:oasis:names:tc:opendocument:xmlns:drawing:1.0" xmlns:x="http://www.w3.org/1999/xlink" xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0">
	
	<xsl:output omit-xml-declaration="yes"/>

	<xsl:template match="/">
		<xsl:apply-templates>
			<xsl:with-param name="depth">0</xsl:with-param>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="*">
		<xsl:param name="depth"/>
		
		<xsl:call-template name="tab">
			<xsl:with-param name="depth" select="0"/>
			<xsl:with-param name="maxDepth" select="$depth"/>
		</xsl:call-template>
		
		<xsl:element name="span">
			<xsl:attribute name="class">xml-element</xsl:attribute>
			<xsl:text>&lt;</xsl:text><xsl:value-of select="name()"/>
		</xsl:element>
		<xsl:if test="@*">
			<xsl:text> </xsl:text>
			<xsl:apply-templates select="@*"/>
		</xsl:if>
		<xsl:choose>
			<xsl:when test="* or text() != ''">
				<xsl:element name="span">
					<xsl:text>&gt;</xsl:text>
				</xsl:element>
				<xsl:if test="*">
					<xsl:element name="br"/>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="*">
						<xsl:apply-templates>
							<xsl:with-param name="depth" select="$depth + 1"/>
						</xsl:apply-templates>
						<xsl:call-template name="tab">
							<xsl:with-param name="depth" select="0"/>
							<xsl:with-param name="maxDepth" select="$depth"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:element name="span">
							<xsl:attribute name="action"><xsl:value-of select="@_action"/></xsl:attribute>
							<xsl:attribute name="match"><xsl:value-of select="@_match"/></xsl:attribute>
							<xsl:apply-templates>
								<xsl:with-param name="depth" select="$depth + 1"/>
							</xsl:apply-templates>
						</xsl:element>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:element name="span">
					<xsl:attribute name="class">xml-element</xsl:attribute>
					<xsl:text>&lt;/</xsl:text><xsl:value-of select="name()"/><xsl:text>&gt;</xsl:text><xsl:element name="br"/>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="span">
					<xsl:attribute name="class">xml-element</xsl:attribute>
					<xsl:text>/&gt;</xsl:text><xsl:element name="br"/>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="@*">
		<xsl:if test="not(name() = '_id') and not(name() = '_match') and not(name() = '_action') and not(name() = '_matchId') and not(name() = '_offset')">
			<xsl:element name="span">
				<xsl:attribute name="class">xml-attribute-key</xsl:attribute>
				<xsl:value-of select="name()"/>
			</xsl:element>
			<xsl:text> = </xsl:text>
			<xsl:element name="span">
				<xsl:attribute name="class">xml-attribute-value</xsl:attribute>
				<xsl:text>"</xsl:text><xsl:value-of select="."/><xsl:text>" </xsl:text>
			</xsl:element>
		</xsl:if>
	</xsl:template>
		
	<xsl:template match="text()"><xsl:value-of select="normalize-space()"/></xsl:template>
		
	<xsl:template name="tab">
		<xsl:param name="depth"/>
		<xsl:param name="maxDepth"/>
		<xsl:text>	</xsl:text>
		<xsl:if test="$depth &lt; $maxDepth">
			<xsl:call-template name="tab">
				<xsl:with-param name="depth" select="$depth + 1"/>
				<xsl:with-param name="maxDepth" select="$maxDepth"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>