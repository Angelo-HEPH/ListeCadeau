<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Connexion</title>
</head>
<body class="bg-light">

<jsp:include page="/WEB-INF/view/includes/header.jsp" />

<div class="container my-5">

    <div class="bg-white shadow-lg rounded-4 p-5 mx-auto" style="max-width: 400px;">

        <h2 class="text-center fw-bold mb-4">🔑 Connexion</h2>
        <p class="text-center text-muted mb-4">
            Connectez-vous pour accéder à vos listes de cadeaux 🎁
        </p>

        <% 
            String success = (String) session.getAttribute("successMessage");
            if (success != null) {
        %>
            <div class="alert alert-success text-center fw-bold">
                <%= success %>
            </div>
        <%
                session.removeAttribute("successMessage");
            }
        %>

        <% 
            String error = (String) request.getAttribute("error");
            if (error != null) {
        %>
            <div class="alert alert-danger text-center fw-bold">
                <%= error %>
            </div>
        <% } %>

        <form action="login" method="post" class="mt-3">

            <div class="mb-3">
                <label class="form-label fw-semibold">✉️ Email</label>
                <input type="email" class="form-control form-control-lg" name="email" 
                       value="<%= request.getAttribute("email") != null ? request.getAttribute("email") : "" %>" 
                       required>
            </div>

            <div class="mb-3">
                <label class="form-label fw-semibold">🔒 Mot de passe</label>
                <input type="password" class="form-control form-control-lg" name="password" required>
            </div>

            <button type="submit" class="btn btn-primary btn-lg w-100 fw-bold mt-3">
                Se connecter
            </button>

        </form>

        <div class="text-center mt-3">
            <a href="<%= request.getContextPath() %>/createAccount" class="text-decoration-none">
                Pas encore de compte ? Créez-en un 🎁
            </a>
        </div>

    </div>

</div>

</body>
</html>
