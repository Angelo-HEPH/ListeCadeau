<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Connexion</title>
</head>
<body>

<jsp:include page="/WEB-INF/view/includes/header.jsp" />

<div class="container">

    <h2>Connexion</h2>

    <% 
        String success = (String) session.getAttribute("successMessage");
        if (success != null) {
    %>
        <p class="text-success fw-bold"><%= success %></p>
    <%
            session.removeAttribute("successMessage");
        }
    %>

    <% 
        String error = (String) request.getAttribute("error");
        if (error != null) {
    %>
        <p class="text-danger fw-bold"><%= error %></p>
    <%
        }
    %>

    <form action="login" method="post" class="mb-3">

        <label>Email :</label>
        <input type="email" class="form-control" name="email" required>

        <label class="mt-2">Mot de passe :</label>
        <input type="password" class="form-control" name="password" required>

        <button type="submit" class="btn btn-primary mt-3">Se connecter</button>
    </form>

</div>

</body>
</html>
