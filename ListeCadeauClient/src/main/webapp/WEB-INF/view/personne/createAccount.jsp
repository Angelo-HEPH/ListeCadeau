<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Créer un compte</title>
</head>
<body class="bg-light">

    <jsp:include page="/WEB-INF/view/includes/header.jsp" />

    <div class="container my-5">

        <div class="bg-white shadow-lg rounded-4 p-5 mx-auto" style="max-width: 600px;">

            <h2 class="text-center fw-bold mb-4">🎉 Créer un compte</h2>
            <p class="text-center text-muted mb-4">
                Rejoignez l’aventure et commencez à créer vos listes de cadeaux 🎁
            </p>

            <%
                String error = (String) request.getAttribute("error");
                String message = (String) request.getAttribute("message");

                if (error != null && !error.trim().isEmpty()) { 
            %>
                <div class="alert alert-danger text-center fw-bold">
                    <%= error %>
                </div>
            <% } %>

            <% if (message != null && !message.trim().isEmpty()) { %>
                <div class="alert alert-success text-center fw-bold">
                    <%= message %>
                </div>
            <% } %>

            <form action="createAccount" method="post" class="mt-3">

                <h4 class="fw-bold mb-3">👤 Informations personnelles</h4>
                <div class="row g-3 mb-4">

                    <div class="col-md-6">
                        <label class="form-label fw-semibold">👤 Nom :</label>
                        <input type="text" class="form-control form-control-lg" name="name"
                               value="<%= (request.getAttribute("name") != null ? request.getAttribute("name") : "") %>" required>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label fw-semibold">📝 Prénom :</label>
                        <input type="text" class="form-control form-control-lg" name="firstName"
                               value="<%= (request.getAttribute("firstName") != null ? request.getAttribute("firstName") : "") %>" required>
                    </div>

                    <div class="col-md-4">
                        <label class="form-label fw-semibold">🎂 Âge :</label>
                        <input type="number" class="form-control form-control-lg" name="age"
                               value="<%= (request.getAttribute("age") != null ? request.getAttribute("age") : "") %>" required>
                    </div>

                    <div class="col-md-8">
                        <label class="form-label fw-semibold">✉️ Email :</label>
                        <input type="email" class="form-control form-control-lg" name="email"
                               value="<%= (request.getAttribute("email") != null ? request.getAttribute("email") : "") %>" required>
                    </div>

                    <div class="col-12">
                        <label class="form-label fw-semibold">🔒 Mot de passe :</label>
                        <input type="password" class="form-control form-control-lg" name="password" required>
                    </div>

                </div>

                <h4 class="fw-bold mb-3">🏠 Adresse</h4>
                <div class="row g-3 mb-4">

                    <div class="col-md-8">
                        <label class="form-label fw-semibold">Rue :</label>
                        <input type="text" class="form-control form-control-lg" name="street"
                               value="<%= (request.getAttribute("street") != null ? request.getAttribute("street") : "") %>" required>
                    </div>

                    <div class="col-md-4">
                        <label class="form-label fw-semibold"># Numéro :</label>
                        <input type="text" class="form-control form-control-lg" name="streetNumber"
                               value="<%= (request.getAttribute("streetNumber") != null ? request.getAttribute("streetNumber") : "") %>" required>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label fw-semibold">🏙 Ville :</label>
                        <input type="text" class="form-control form-control-lg" name="city"
                               value="<%= (request.getAttribute("city") != null ? request.getAttribute("city") : "") %>" required>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label fw-semibold">📮 Code postal :</label>
                        <input type="number" class="form-control form-control-lg" name="postalCode"
                               value="<%= (request.getAttribute("postalCode") != null ? request.getAttribute("postalCode") : "") %>" required>
                    </div>

                </div>

                <div class="d-flex justify-content-between">
                    <button type="submit" class="btn btn-primary fw-bold btn-lg">
                        Créer le compte
                    </button>
                    <a href="login" class="btn btn-secondary fw-bold btn-lg">
                        ← Retour
                    </a>
                </div>

            </form>

        </div>

    </div>

</body>
</html>
