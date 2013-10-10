<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="html" encoding="UTF-8"/>

  <xsl:variable name="url"><xsl:value-of select="doc/@url"/></xsl:variable>
    <xsl:template match="/">

<html>
  <head>
    <title>Agent API</title>
    <meta content="author" value="Rui Pedro Lopes"/>
  </head>
  <link rel="stylesheet" type="text/css" href="agentapi.css"/>
  <body bgcolor="#ffffff">
    <xsl:apply-templates select="//banner"/>
    <xsl:apply-templates select="//smi"/>
  </body>
  </html>

  </xsl:template>


  <xsl:template match="banner">
    
<table width="100%" bgcolor="#eeeeee" cellspacing="0" cellpadding="5" border="0">
  <tr>
    <th align="left">
      User: <xsl:value-of select="@user"/>
    </th>
    <form method="post">
    <th align="right" valign="middle">
      <input type="hidden" name="action" value="logout"/>
      <input type="submit" name="logout" value="logout"/>
    </th>
    </form>
  </tr>
</table>

  </xsl:template>

  <xsl:template match="smi">
    <p><b>MIB</b></p>
    <xsl:apply-templates select="node"/>
    <xsl:apply-templates select="scalar"/>
    <xsl:apply-templates select="table"/>
    <p><b>End of MIB</b></p>
  </xsl:template>

  <xsl:template match="node">
<p>
<table border="0" cellpadding="2" width="100%">
  <tr>
   <td bgcolor="#EEEEEE">
    <a href="{$url}?page={@name}"><xsl:value-of select="@name"/></a>
    (<a href="{$url}?page={@name}&amp;up=true">parent</a>)
   </td>
   <td bgcolor="#EEEEEE">
    <xsl:value-of select="@oid"/>
   </td>
  </tr>
  <xsl:variable name="descr"><xsl:value-of select="description"/></xsl:variable>
  <xsl:if test="$descr != 'null'">
    <tr>
     <td colspan="2" bgcolor="#EEEEEE">
      <pre><xsl:value-of select="description"/></pre>
     </td>
    </tr>
  </xsl:if>
</table>
</p>

  </xsl:template>

  <xsl:template match="scalar">
<p>
<table border="0" celpadding="2" width="100%">
  <tr>
   <td width="40%" bgcolor="#EEEEEE">
    <a href="{$url}?page={@name}"><xsl:value-of select="@name"/></a>
    (<a href="{$url}?page={@name}&amp;up=true">parent</a>)
   </td>
   <td width="40%" bgcolor="#EEEEEE">
    <xsl:value-of select="//type/@name"/>
   </td>
   <td width="20%" bgcolor="#EEEEEE">
    <xsl:value-of select="access"/>
   </td>
  </tr>
  <xsl:variable name="descr"><xsl:value-of select="description"/></xsl:variable>
  <xsl:if test="$descr != 'null'">
    <tr>
     <td colspan="3" bgcolor="#EEEEEE">
      <pre><xsl:value-of select="description"/></pre>
     </td>
    </tr>
  </xsl:if>

  <tr>

  <xsl:variable name="access"><xsl:value-of select="access"/></xsl:variable>
  <xsl:if test="$access='read-write' or $access='read-create' or $access='write-only'">
  <form method="post" enctype="application/x-www-form-urlencoded" name="{value/@oid}">

   <td width="40%" valign="middle" bgcolor="#555555">
     <xsl:variable name="oid"><xsl:value-of select="value/@oid"/></xsl:variable>
     <xsl:if test="boolean($oid)">
       <font color="#ffffff"><b>OID:</b></font><input type="text" size="30" name="oid" value="{$oid}"/>
     </xsl:if>
     <xsl:if test="not(boolean($oid))">
     <font color="#ffffff"><b>OID:</b></font><input type="text" size="30" name="oid" value="{@oid}"/>
     </xsl:if>
   </td>
   <td width="40%" valign="middle" bgcolor="#555555">
      <xsl:variable name="value"><xsl:value-of select="value"/></xsl:variable>
      <font color="#ffffff"><b>Value:</b></font><input type="text" size="30" name="value" value="{$value}"/>
   </td>
   <td width="20%" align="right" bgcolor="#555555">
      <input type="hidden" name="action" value="snmpop"/>
      <input type="submit" name="set" value="Set"/>
    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
   </td>
  </form>
  </xsl:if>

  <xsl:if test="not($access='read-write' or $access='read-create' or $access='write-only')">
   <td width="40%" bgcolor="#EEEEEE">
    <b>OID:</b><xsl:value-of select="@oid"/>
   </td>
   <td width="40%" bgcolor="#EEEEEE">
    <b>Value:</b><xsl:value-of select="value"/>
   </td>
   <td width="20%" align="right" bgcolor="#EEEEEE">
    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
   </td>
  </xsl:if>

  </tr>
</table>
</p>

  </xsl:template>

  <xsl:template match="table">
<p>
    <a href="{$url}?page={@name}"><xsl:value-of select="@name"/></a>
    (<a href="{$url}?page={@name}&amp;up=true">parent</a>)

<table border="0" celpadding="2" width="100%">
  <tr>
    <xsl:apply-templates select="row"/>
    <xsl:apply-templates select="valuerow"/>
  </tr>
</table>
</p>

  </xsl:template>

  <xsl:template match="row">
    <xsl:for-each select="column">
      <th style="background-color: #EEEEEE">
        <xsl:variable name="access"><xsl:value-of select="access"/></xsl:variable>
        <xsl:if test="$access='read-write' or $access='read-create' or $access='write-only'">
          <a href="{$url}?page={@name}"><xsl:value-of select="@name"/></a>
        </xsl:if>
        <xsl:if test="not($access='read-write' or $access='read-create' or $access='write-only')">
          <font class="disabled"><xsl:value-of select="@name"/></font>
        </xsl:if>
      </th>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="valuerow">
    <tr>
    <xsl:for-each select="cell">
      <td style="background-color: #EEEEEE">
        <xsl:variable name="access"><xsl:value-of select="access"/></xsl:variable>
        <xsl:if test="$access='read-write' or $access='read-create' or $access='write-only'">
          <a href="{$url}?page={@name}&amp;oid={@oid}"><xsl:value-of select="value"/></a>
        </xsl:if>
        <xsl:if test="not($access='read-write' or $access='read-create' or $access='write-only')">
          <font class="disabled"><xsl:value-of select="value"/></font><br/>
          <xsl:if test="not(boolean(value))">
          <font class="disabled"><xsl:value-of select="@oid"/></font>
          </xsl:if>
        </xsl:if>
      </td>
    </xsl:for-each>
    </tr>
  </xsl:template>

</xsl:stylesheet>
