<%@ page language="java" pageEncoding="UTF-8"
    import="com.chn.fs.util.*"
    import="com.chn.fs.*"
    import="java.io.*"
%><%
String id=request.getParameter("id");
if(id == null || id.length() == 0) return;
byte[] digest = MD.decodeHexString(id);
byte[] result = client.read(digest);

if(result == null) return;

response.setContentLength(result.length);
OutputStream os = response.getOutputStream();
os.write(result);
os.close();
%><%!
Cfg cfg = Cfg.getCfg("/fileserver.properties");
Integer port = cfg.get(Integer.class, "server.port");
FSClient client = new FSClient("127.0.0.1", port);
%>