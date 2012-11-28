<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>Submit a script</title>
  </head>
<body>
  <% String result = (String) request.getAttribute("result"); %>
  <form action="/data/edit" method="post">
    <p>
      <label>
        <textarea rows="10" cols="100" name="script"></textarea>
      </label>
    </p>
    <p>
      <label>
        Result: <% out.print(result); %>
      </label>
    </p>
    <p>
      <input type="submit"/>
    </p>
  </form>
</body>
</html>