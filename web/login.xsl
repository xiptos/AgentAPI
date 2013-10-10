<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="html" encoding="UTF-8"/>

  <xsl:template match="login">

<html>
<head>
<title>AgentAPI</title>
</head>
<link rel="stylesheet" type="text/css" href="agentapi.css"/>
<body bgcolor="#FFFFFF">

<font face="Arial, Helvetica">
<p/>
<p align="center"><img border="0" src="images/agentapi.gif"/></p>

<table border="0" align="center" width="40%" cellpadding="2" cellspacing="0">
<tr><td bgcolor="#002266" align="left">
<font color="#FFFFFF" face="Arial, Helvetica" size="3"><b>LOGIN</b></font>
</td></tr><tr><td align="center" valign="middle" bgcolor="#EEEEEE">
<form method="post" enctype="application/x-www-form-urlencoded" name="login">
<input type="hidden" name="action" value="login"/>
<br/>
UserID: <input type="text" name="userid"  size="10"/>
<br/>
Password: <input type="password" name="passwd"  size="10"/>
<br/>
<input type="submit" name="Login" value="Login"/> <input type="reset" value="Clear"/>
</form>
</td></tr></table>
<p align="center"><font size="1"><br/>
Test Agent
</font></p></font>
</body></html>

  </xsl:template>
</xsl:stylesheet>
