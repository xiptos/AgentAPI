<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="yes" encoding="UTF-8" media-type = "text/vnd.wap.wml" omit-xml-declaration = "no" doctype-public = "-//WAPFORUM//DTD WML 1.1//E
N" doctype-system = "http://www.wapforum.org/DTD/wml_1.1.xml" />

    <xsl:variable name="url"><xsl:value-of select="doc/@url"/></xsl:variable>

    <xsl:template match="/">

<wml>
  <card id="card1" title="MIB">
    <xsl:apply-templates select="//banner"/>
    <xsl:apply-templates select="//smi"/>
  </card>
</wml>

  </xsl:template>


  <xsl:template match="banner">
    
  <p>
    <xsl:value-of select="@user"/> - 
    <anchor title="logout">logout
      <go href="{$url}?wml=true" sendreferer="true" method="post">
        <postfield name="action" value="logout"/>
      </go>
    </anchor>
    <br/>
  </p>

  </xsl:template>

  <xsl:template match="smi">
    <p align="center">MIB</p>
    <xsl:apply-templates select="//node"/>
    <xsl:apply-templates select="//scalar"/>
    <xsl:apply-templates select="//table"/>
    <p align="center">End of MIB</p>
  </xsl:template>

  <xsl:template match="node">

<p>
    <a href="{$url}?page={@name}&amp;wml=true"><xsl:value-of select="@name"/></a>
    (<a href="{$url}?page={@name}&amp;wml=true&amp;up=true">parent</a>)
    <br/>
    <xsl:value-of select="@oid"/>
</p>

  </xsl:template>

  <xsl:template match="scalar">
  <p>
  <table columns="2">
    <tr>
      <td><a href="{$url}?page={@name}&amp;wml=true"><xsl:value-of select="@name"/></a>
    (<a href="{$url}?page={@name}&amp;wml=true&amp;up=true">parent</a>)</td>
      <td><xsl:value-of select="//type/@name"/></td>
    </tr>
  </table>
  </p>
  <xsl:variable name="access"><xsl:value-of select="access"/></xsl:variable>
  <xsl:if test="$access='read-write' or $access='read-create'">
   <xsl:variable name="oid"><xsl:value-of select="//value/@oid"/></xsl:variable>
   <xsl:if test="boolean($oid)">
     <p>OID:<input type="text" name="oid" value="{$oid}"/></p>
   </xsl:if>
   <xsl:if test="not(boolean($oid))">
     <p>OID:<input type="text" name="oid" value="{@oid}"/></p>
   </xsl:if>
      
   <xsl:variable name="value"><xsl:value-of select="value"/></xsl:variable>
     <p>Value:<input type="text" name="value" value=" {$value}"/></p>
  <p align="center">
    <anchor title="snmpop">submit                 
      <go href="{$url}?wml=true" sendreferer="true" method="post">
        <postfield name="action" value="snmpop"/>
        <postfield name="value" value="$(value)"/>
        <postfield name="oid" value="$(oid)"/>
      </go>
    </anchor>
  </p>

  </xsl:if>

  <xsl:if test="not($access='read-write' or $access='read-create')">
  <p>
  <table columns="2">
  <tr>
   <td>
    OID:<xsl:value-of select="@oid"/>
   </td>
   <td>
    Value:<xsl:value-of select="value"/>
   </td>
  </tr>
</table>
  </p>
  </xsl:if>

  </xsl:template>

  <xsl:template match="table">
<p>
    <a href="{$url}?page={@name}&amp;wml=true"><xsl:value-of select="@name"/></a>
    (<a href="{$url}?page={@name}&amp;wml=true&amp;up=true">parent</a>)<br/>

  <xsl:apply-templates select="row"/>
</p>

  </xsl:template>

  <xsl:template match="row">

    <xsl:for-each select="//column">
        <xsl:variable name="access"><xsl:value-of select="access"/></xsl:variable>
        <xsl:if test="$access='read-write' or $access='read-create'">
          <a href="{$url}?page={@name}&amp;wml=true"><xsl:value-of select="@name"/></a><br/>
        </xsl:if>
        <xsl:if test="not($access='read-write' or $access='read-create')">
          <xsl:value-of select="@name"/><br/>
        </xsl:if>
    </xsl:for-each>
  </xsl:template>

</xsl:stylesheet>
