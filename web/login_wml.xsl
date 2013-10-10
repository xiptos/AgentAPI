<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="yes" encoding="UTF-8" media-type = "text/vnd.wap.wml" omit-xml-declaration = "no" doctype-public = "-//WAPFORUM//DTD WML 1.1//EN" doctype-system = "http://www.wapforum.org/DTD/wml_1.1.xml" />

  <xsl:template match="login">

<wml> 
 
  <!-- Possible <head> element here. --> 
   
  <card id="card1" title="Login"> 
    <p align="center"> 
      <em>
      <b>AgentAPI</b> - By rlopes<br/>
      </em>
      <!-- Card implementation here. --> 
      UserID: <input type="text" name="userid"/>
      Password: <input type="password" name="passwd"/>
    </p> 
     
    <!-- Possible <onevent> elements here. --> 

  <p align="center">
    <anchor title="login">login
      <go href="agentapi?wml=true" sendreferer="true" method="post">
        <postfield name="action" value="login"/>
        <postfield name="userid" value="$(userid)"/>
        <postfield name="passwd" value="$(passwd)"/>
      </go>
    </anchor>
  </p>


     
    <!-- Possible <timer> element here. --> 
     
    <!-- Additional <p> elements here. --> 
  </card> 
   
  <!-- Additional <card> elements here. --> 
 
</wml> 

  </xsl:template>
</xsl:stylesheet>
