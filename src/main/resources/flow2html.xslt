<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:param name="flowName"/>

	<xsl:template match="/FLOW">
		<xsl:element name="h1">Service: <xsl:value-of select="$flowName"/></xsl:element>
		<xsl:element name="ul">
			<xsl:attribute name="class">service_root</xsl:attribute>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="MAP">
		<xsl:variable name="id"><xsl:value-of select="@_id"/></xsl:variable>
		<xsl:element name="li">
			<xsl:attribute name="class">map</xsl:attribute>
			<xsl:attribute name="onClick">toggle('<xsl:value-of select='$id'/>')</xsl:attribute>
			<xsl:if test="@NAME">
				<xsl:element name="span">
					<xsl:attribute name="class">name</xsl:attribute>
					<xsl:value-of select="@NAME"/><xsl:text>: </xsl:text>
				</xsl:element>
			</xsl:if>
			<xsl:element name="span">
				<xsl:choose>
					<xsl:when test="@_match = 'false'">
						<xsl:choose>
							<xsl:when test="@_action='insert'">
								<xsl:attribute name="style">background-color:#159600;color:#FFFFFF</xsl:attribute>
							</xsl:when>
							<xsl:when test="@_action='delete'">
								<xsl:attribute name="style">background-color:#930000;color:#FFFFFF</xsl:attribute>
							</xsl:when>
						</xsl:choose>
					</xsl:when>
				</xsl:choose>
				<xsl:attribute name="class">command</xsl:attribute>
				<xsl:text>MAP</xsl:text>
			</xsl:element>
			<xsl:call-template name="comment">
				<xsl:with-param name="element" select="."/>
			</xsl:call-template>
		</xsl:element>
		
		<xsl:if test="*[@_id] or *[@_match='false']">
			<xsl:element name="li">
				<xsl:attribute name="class">container</xsl:attribute>
				<xsl:element name="ul">
					<xsl:attribute name="id"><xsl:value-of select="$id"/></xsl:attribute>
					<xsl:apply-templates/>
				</xsl:element>
			</xsl:element>
		</xsl:if>

	</xsl:template>
	
	<xsl:template match="MAPDELETE">
		<xsl:variable name="id"><xsl:value-of select="@_id"/></xsl:variable>
		<xsl:element name="li">
			<xsl:element name="span">
				<xsl:choose>
					<xsl:when test="@_match = 'false'">
						<xsl:choose>
							<xsl:when test="@_action='insert'">
								<xsl:attribute name="style">background-color:#159600;color:#FFFFFF</xsl:attribute>
							</xsl:when>
							<xsl:when test="@_action='delete'">
								<xsl:attribute name="style">background-color:#930000;color:#FFFFFF</xsl:attribute>
							</xsl:when>
						</xsl:choose>
					</xsl:when>
				</xsl:choose>
				<xsl:attribute name="class">command</xsl:attribute>
				<xsl:text>DROP </xsl:text><xsl:value-of select="@FIELD"/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="MAPCOPY">
		<xsl:variable name="id"><xsl:value-of select="@_id"/></xsl:variable>
		<xsl:element name="li">
			<xsl:element name="span">
				<xsl:choose>
					<xsl:when test="@_match = 'false'">
						<xsl:choose>
							<xsl:when test="@_action='insert'">
								<xsl:attribute name="style">background-color:#159600;color:#FFFFFF</xsl:attribute>
							</xsl:when>
							<xsl:when test="@_action='delete'">
								<xsl:attribute name="style">background-color:#930000;color:#FFFFFF</xsl:attribute>
							</xsl:when>
						</xsl:choose>
					</xsl:when>
				</xsl:choose>
				<xsl:attribute name="class">command</xsl:attribute>
				<xsl:text>COPY </xsl:text><xsl:value-of select="@FROM"/><xsl:text> to </xsl:text><xsl:value-of select="@TO"/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="MAPSET">
		<xsl:variable name="id"><xsl:value-of select="@_id"/></xsl:variable>
		<xsl:element name="li">
			<xsl:element name="span">
				<xsl:choose>
					<xsl:when test="@_match = 'false'">
						<xsl:choose>
							<xsl:when test="@_action='insert'">
								<xsl:attribute name="style">background-color:#159600;color:#FFFFFF</xsl:attribute>
							</xsl:when>
							<xsl:when test="@_action='delete'">
								<xsl:attribute name="style">background-color:#930000;color:#FFFFFF</xsl:attribute>
							</xsl:when>
						</xsl:choose>
					</xsl:when>
				</xsl:choose>
				<xsl:attribute name="class">command</xsl:attribute>
				<xsl:text>SET VALUE </xsl:text><xsl:value-of select="@FIELD"/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="INVOKE">
		<xsl:variable name="id"><xsl:value-of select="@_id"/></xsl:variable>
		<xsl:element name="li">
			<xsl:attribute name="class">invoke</xsl:attribute>
			<xsl:attribute name="onClick">toggle('<xsl:value-of select='$id'/>')</xsl:attribute>
			<xsl:if test="@NAME">
				<xsl:element name="span">
					<xsl:attribute name="class">name</xsl:attribute>
					<xsl:value-of select="@NAME"/><xsl:text>: </xsl:text>
				</xsl:element>
			</xsl:if>
			<xsl:element name="span">
				<xsl:choose>
					<xsl:when test="@_match = 'false'">
						<xsl:choose>
							<xsl:when test="@_action='insert'">
								<xsl:attribute name="style">background-color:#159600;color:#FFFFFF</xsl:attribute>
							</xsl:when>
							<xsl:when test="@_action='delete'">
								<xsl:attribute name="style">background-color:#930000;color:#FFFFFF</xsl:attribute>
							</xsl:when>
						</xsl:choose>
					</xsl:when>
				</xsl:choose>
				<xsl:attribute name="class">command</xsl:attribute>
				<xsl:text>INVOKE </xsl:text>
			</xsl:element>
			<xsl:element name="a">
				<xsl:attribute name="href">index.dsp?service=<xsl:value-of select="@SERVICE"/></xsl:attribute>
				<xsl:value-of select="@SERVICE"/>
			</xsl:element>
			<xsl:call-template name="comment">
				<xsl:with-param name="element" select="."/>
			</xsl:call-template>
		</xsl:element>
		
		<xsl:if test="*[@_id] or *[@_match='false']">
			<xsl:element name="li">
				<xsl:attribute name="class">container</xsl:attribute>
				<xsl:element name="ul">
					<xsl:attribute name="id"><xsl:value-of select="$id"/></xsl:attribute>
					<xsl:apply-templates/>
				</xsl:element>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="MAPINVOKE">
		<xsl:variable name="id"><xsl:value-of select="@_id"/></xsl:variable>
		<xsl:element name="li">
			<xsl:attribute name="class">invoke</xsl:attribute>
			<xsl:attribute name="onClick">toggle('<xsl:value-of select='$id'/>')</xsl:attribute>
			<xsl:if test="@NAME">
				<xsl:element name="span">
					<xsl:attribute name="class">name</xsl:attribute>
					<xsl:value-of select="@NAME"/><xsl:text>: </xsl:text>
				</xsl:element>
			</xsl:if>
			<xsl:element name="span">
				<xsl:choose>
					<xsl:when test="@_match = 'false'">
						<xsl:choose>
							<xsl:when test="@_action='insert'">
								<xsl:attribute name="style">background-color:#159600;color:#FFFFFF</xsl:attribute>
							</xsl:when>
							<xsl:when test="@_action='delete'">
								<xsl:attribute name="style">background-color:#930000;color:#FFFFFF</xsl:attribute>
							</xsl:when>
						</xsl:choose>
					</xsl:when>
				</xsl:choose>
				<xsl:attribute name="class">command</xsl:attribute>
				<xsl:text>TRANSFORMER </xsl:text>
			</xsl:element>
			<xsl:element name="a">
				<xsl:attribute name="href">index.dsp?service=<xsl:value-of select="@SERVICE"/></xsl:attribute>
				<xsl:value-of select="@SERVICE"/>
			</xsl:element>
		</xsl:element>
		
		<xsl:if test="*[@_id] or *[@_match='false']">
			<xsl:element name="li">
				<xsl:attribute name="class">container</xsl:attribute>
				<xsl:element name="ul">
					<xsl:attribute name="id"><xsl:value-of select="$id"/></xsl:attribute>
					<xsl:apply-templates/>
				</xsl:element>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="LOOP">
		<xsl:variable name="id"><xsl:value-of select="@_id"/></xsl:variable>
		<xsl:element name="li">
			<xsl:attribute name="class">loop</xsl:attribute>
			<xsl:attribute name="onClick">toggle('<xsl:value-of select='$id'/>')</xsl:attribute>
			<xsl:if test="@NAME">
				<xsl:element name="span">
					<xsl:attribute name="class">name</xsl:attribute>
					<xsl:value-of select="@NAME"/><xsl:text>: </xsl:text>
				</xsl:element>
			</xsl:if>
			<xsl:element name="span">
				<xsl:choose>
					<xsl:when test="@_match = 'false'">
						<xsl:choose>
							<xsl:when test="@_action='insert'">
								<xsl:attribute name="style">background-color:#159600;color:#FFFFFF</xsl:attribute>
							</xsl:when>
							<xsl:when test="@_action='delete'">
								<xsl:attribute name="style">background-color:#930000;color:#FFFFFF</xsl:attribute>
							</xsl:when>
						</xsl:choose>
					</xsl:when>
				</xsl:choose>
				<xsl:attribute name="class">command</xsl:attribute>
				<xsl:text>LOOP</xsl:text>
			</xsl:element>
			<xsl:if test="@IN-ARRAY">
				<xsl:text> over </xsl:text><xsl:value-of select="@IN-ARRAY"/>
			</xsl:if>
			<xsl:call-template name="comment">
				<xsl:with-param name="element" select="."/>
			</xsl:call-template>
		</xsl:element>
		<xsl:if test="*">
			<xsl:element name="li">
				<xsl:attribute name="class">container</xsl:attribute>
				<xsl:element name="ul">
					<xsl:attribute name="id"><xsl:value-of select="$id"/></xsl:attribute>
					<xsl:apply-templates/>
				</xsl:element>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="RETRY">
		<xsl:variable name="id"><xsl:value-of select="@_id"/></xsl:variable>
		<xsl:element name="li">
			<xsl:attribute name="class">retry</xsl:attribute>
			<xsl:attribute name="onClick">toggle('<xsl:value-of select='$id'/>')</xsl:attribute>
			<xsl:if test="@NAME">
				<xsl:element name="span">
					<xsl:attribute name="class">name</xsl:attribute>
					<xsl:value-of select="@NAME"/><xsl:text>: </xsl:text>
				</xsl:element>
			</xsl:if>
			<xsl:element name="span">
				<xsl:choose>
					<xsl:when test="@_match = 'false'">
						<xsl:choose>
							<xsl:when test="@_action='insert'">
								<xsl:attribute name="style">background-color:#159600;color:#FFFFFF</xsl:attribute>
							</xsl:when>
							<xsl:when test="@_action='delete'">
								<xsl:attribute name="style">background-color:#930000;color:#FFFFFF</xsl:attribute>
							</xsl:when>
						</xsl:choose>
					</xsl:when>
				</xsl:choose>
				<xsl:attribute name="class">command</xsl:attribute>
				<xsl:text>RETRY</xsl:text>
			</xsl:element>
			<xsl:if test="@COUNT != ''">
				<xsl:value-of select="@COUNT"/><xsl:text> times </xsl:text>
			</xsl:if>
			<xsl:if test="@LOOP-ON != ''">
				<xsl:text> on </xsl:text><xsl:value-of select="@LOOP-ON"/>
			</xsl:if>
			<xsl:call-template name="comment">
				<xsl:with-param name="element" select="."/>
			</xsl:call-template>
		</xsl:element>
		<xsl:if test="*">
			<xsl:element name="li">
				<xsl:attribute name="class">container</xsl:attribute>
				<xsl:element name="ul">
					<xsl:attribute name="id"><xsl:value-of select="$id"/></xsl:attribute>
					<xsl:apply-templates/>
				</xsl:element>
			</xsl:element>	
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="SEQUENCE">
		<xsl:variable name="id"><xsl:value-of select="@_id"/></xsl:variable>
		<xsl:element name="li">
			<xsl:attribute name="class">sequence</xsl:attribute>
			<xsl:attribute name="onClick">toggle('<xsl:value-of select='$id'/>')</xsl:attribute>
			<xsl:if test="@NAME">
				<xsl:element name="span">
					<xsl:attribute name="class">name</xsl:attribute>
					<xsl:value-of select="@NAME"/><xsl:text>: </xsl:text>
				</xsl:element>
			</xsl:if>
			<xsl:element name="span">
				<xsl:choose>
					<xsl:when test="@_match = 'false'">
						<xsl:choose>
							<xsl:when test="@_action='insert'">
								<xsl:attribute name="style">background-color:#159600;color:#FFFFFF</xsl:attribute>
							</xsl:when>
							<xsl:when test="@_action='delete'">
								<xsl:attribute name="style">background-color:#930000;color:#FFFFFF</xsl:attribute>
							</xsl:when>
						</xsl:choose>
					</xsl:when>
				</xsl:choose>
				<xsl:attribute name="class">command</xsl:attribute>
				<xsl:text>SEQUENCE</xsl:text>
			</xsl:element>
			<xsl:call-template name="comment">
				<xsl:with-param name="element" select="."/>
			</xsl:call-template>
			<xsl:text> (exit on '</xsl:text><xsl:value-of select="@EXIT-ON"/><xsl:text>')</xsl:text>
		</xsl:element>
		<xsl:if test="*">
			<xsl:element name="li">
				<xsl:attribute name="class">container</xsl:attribute>
				<xsl:element name="ul">
					<xsl:attribute name="id"><xsl:value-of select="$id"/></xsl:attribute>
					<xsl:apply-templates/>
				</xsl:element>
			</xsl:element>		
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="EXIT">
		<xsl:variable name="id"><xsl:value-of select="@_id"/></xsl:variable>
		<xsl:element name="li">
			<xsl:attribute name="class">exit</xsl:attribute>
			<xsl:if test="@NAME">
				<xsl:element name="span">
					<xsl:attribute name="class">name</xsl:attribute>
					<xsl:value-of select="@NAME"/><xsl:text>: </xsl:text>
				</xsl:element>
			</xsl:if>
			<xsl:element name="span">
				<xsl:choose>
					<xsl:when test="@_match = 'false'">
						<xsl:choose>
							<xsl:when test="@_action='insert'">
								<xsl:attribute name="style">background-color:#159600;color:#FFFFFF</xsl:attribute>
							</xsl:when>
							<xsl:when test="@_action='delete'">
								<xsl:attribute name="style">background-color:#930000;color:#FFFFFF</xsl:attribute>
							</xsl:when>
						</xsl:choose>
					</xsl:when>
				</xsl:choose>
				<xsl:attribute name="class">command</xsl:attribute>
				<xsl:text>EXIT</xsl:text>
			</xsl:element>
			<xsl:text> (</xsl:text><xsl:value-of select="@SIGNAL"/><xsl:text>)</xsl:text>
			<xsl:if test="@FAILURE-MESSAGE != ''">
				<xsl:text> with message "</xsl:text>
				<xsl:element name="span">
					<xsl:if test="@_action='update' and not(COMMENT/@_action)">
						<xsl:attribute name="style">background-color:#fffbbc</xsl:attribute>
					</xsl:if>
					<xsl:value-of select="@FAILURE-MESSAGE"/>
				</xsl:element>
				<xsl:text>"</xsl:text>
			</xsl:if>
			<xsl:call-template name="comment">
				<xsl:with-param name="element" select="."/>
			</xsl:call-template>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="BRANCH">
		<xsl:variable name="id"><xsl:value-of select="@_id"/></xsl:variable>
		<xsl:element name="li">
			<xsl:attribute name="class">branch</xsl:attribute>
			<xsl:attribute name="onClick">toggle('<xsl:value-of select='$id'/>')</xsl:attribute>
			<xsl:if test="@NAME">
				<xsl:element name="span">
					<xsl:attribute name="class">name</xsl:attribute>
					<xsl:value-of select="@NAME"/><xsl:text>: </xsl:text>
				</xsl:element>
			</xsl:if>
			<xsl:element name="span">
				<xsl:choose>
					<xsl:when test="@_match = 'false'">
						<xsl:choose>
							<xsl:when test="@_action='insert'">
								<xsl:attribute name="style">background-color:#159600;color:#FFFFFF</xsl:attribute>
							</xsl:when>
							<xsl:when test="@_action='delete'">
								<xsl:attribute name="style">background-color:#930000;color:#FFFFFF</xsl:attribute>
							</xsl:when>
						</xsl:choose>
					</xsl:when>
				</xsl:choose>
				<xsl:attribute name="class">command</xsl:attribute>
				<xsl:text>BRANCH</xsl:text>
			</xsl:element>
			<xsl:if test="@SWITCH">
				<xsl:text> on </xsl:text><xsl:value-of select="@SWITCH"/>
			</xsl:if>
			<xsl:call-template name="comment">
				<xsl:with-param name="element" select="."/>
			</xsl:call-template>
		</xsl:element>
		<xsl:if test="*">
			<xsl:element name="li">
				<xsl:attribute name="class">container</xsl:attribute>
				<xsl:element name="ul">
					<xsl:attribute name="id"><xsl:value-of select="$id"/></xsl:attribute>
					<xsl:apply-templates/>
				</xsl:element>
			</xsl:element>	
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="comment">
		<xsl:param name="element"/>
		<xsl:if test="$element/COMMENT and $element/COMMENT != ''">
			<xsl:text>: </xsl:text>
			<xsl:element name="span">
				<xsl:if test="$element/COMMENT/@_action='update'">
					<xsl:attribute name="style">background-color:#fffbbc</xsl:attribute>
				</xsl:if>
				<xsl:value-of select="$element/COMMENT"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	
	
	<!-- 
		The spec for xsl:apply-templates states that if no matching template is found, it uses built-in templates 
		Apparantly, if a (text?) element is found and no match is found in the defined templates, it will simply output the contents! Possibly specific for this xsl engine.
	-->
	<xsl:template match="COMMENT">
		<!-- do nothing -->
	</xsl:template>
	
	<xsl:template match="*">
		<!-- do nothing -->
	</xsl:template>
</xsl:stylesheet>
