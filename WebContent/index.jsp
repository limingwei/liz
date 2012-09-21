<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>liz liz liz</title>
	</head>
	<body>
		<div>
			<a href="?lang=en_US">English</a>
			<a href="?lang=zh_CN">中文</a>
		</div>
		<br/>
		<div>
			${lang["welcome"]} - ${lang.name} - ${lang.sex}
		</div>
		<br/>
		<h2>welcome</h2>
		<a href="fm">fm</a>
		<a href="fm2">fm2</a>
		<a href="vc">vc</a>
		<a href="vc2">vc2</a>
		<a href="bt">bt</a>
		<a href="testjsp">testjsp</a>
		<a href="404">404</a>
		<a href="test_3">test_3</a>
		<br/>
		<a href="json">json</a>
		<a href="xml">xml</a>
		<a href="text">text</a>
		<a href="testViewType">testViewType</a>
		<a href="test_action_path_default_value">test_action_path_default_value</a>		
		<br/>
		<a href="thread-1-2.htm">thread-1-2.htm</a>
		<a href="thread-11-2.htm">thread-11-2.htm</a>
		<a href="thread-1-abc.htm">thread-1-abc.htm</a>
		<a href="thread-1-ABC.htm">thread-1-ABC.htm</a>
		<a href="thread-any.htm">thread-any.htm</a>
		<br/>
		<a href="account_list">account list</a>
		<a href="forum_list">forum_list</a>
		<br/>
		<h2>test NewFilter</h2>		
		<a href="test_ctx">test_ctx</a>
		<a href="test_all">test_all</a>
		<form action="test_new_filter" method="POST">
			<input type="submit" value="submit"/>
		</form>
		<br/>
		<form action="test_dev_filter" method="POST">
			<input name="id" value="1"/>
			<input name="id" value="3"/>
			<input name="id" value="5"/>
			<input name="id" value="7"/>
			<input name="id" value="9"/>
			<input name="int1" value="1"/>
			<input name="int2" value="2"/>
			<input name="str1" value="str1"/>
			<input name="str2" value="str2"/>
			<input name="account1.username" value="account1.username"/>
			<input name="account2.username" value="account2.username"/>
			<input name="bol" value="true"/>
			<input type="submit" value="submit"/>
		</form>
		<h2>test abs action</h2>
		<form action="test_abs_action" method="POST">
			<input name="id" value="1"/>
			<input name="id" value="3"/>
			<input name="id" value="5"/>
			<input name="id" value="7"/>
			<input name="id" value="9"/>
			<input name="int1" value="1"/>
			<input name="int2" value="2"/>
			<input name="str1" value="str1"/>
			<input name="str2" value="str2"/>
			<input name="account1.username" value="account1.username"/>
			<input name="account2.username" value="account2.username"/>
			<input type="submit" value="submit"/>
		</form>
		
		
		<br/><br/><br/><br/><br/>

		<form action="upload" method="post" enctype="multipart/form-data">
			<input type="file" name="file_input_name"/>
			<input type="file" name="file_input_name"/>
			<input type="submit" value="上传"/>
		</form>
			

		<form action="testGet" method="GET">
			<input type="submit" value="testGet/GET"/>
		</form>
		<form action="testGet" method="POST">
			<input type="submit" value="testGet/POST"/>
		</form>
		<form action="testPost" method="GET">
			<input type="submit" value="testPost/GET"/>
		</form>
		<form action="testPost" method="POST">
			<input type="submit" value="testPost/POST"/>
		</form>
		<form action="testAny" method="GET">
			<input type="submit" value="testAny/GET"/>
		</form>
		<form action="testAny" method="POST">
			<input type="submit" value="testAny/POST"/>
		</form>
	</body>
</html>