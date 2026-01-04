<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Créer une liste de cadeaux</title>
</head>

<body class="bg-light">

<jsp:include page="/WEB-INF/view/includes/header.jsp" />

<div class="container my-5">

    <div class="bg-white rounded-4 shadow-lg p-5 mx-auto" style="max-width: 600px;">

        <div class="text-center mb-4">
            <span class="badge bg-primary-subtle text-primary mb-2">
                Nouvelle liste
            </span>
            <h1 class="fw-bold mt-2">
                ➕🎁 Créer une liste
            </h1>
            <p class="text-muted">
                Préparez une liste de cadeaux pour un événement spécial
            </p>
        </div>

        <hr class="my-4">

        <%
            String error = (String) request.getAttribute("error");
            String title = (String) request.getAttribute("title");
            String evenement = (String) request.getAttribute("evenement");
            String expirationDate = (String) request.getAttribute("expirationDate");
        %>

        <% if (error != null) { %>
            <div class="alert alert-danger text-center fw-bold">
                <%= error %>
            </div>
        <% } %>

        <form action="" method="post">

            <div class="mb-3">
                <label class="form-label fw-semibold">📝 Titre</label>
                <input type="text" name="title"
                       class="form-control"
                       placeholder="Ex : Anniversaire de Benoit"
                       required
                       value="<%= (title != null ? title : "") %>">
            </div>

            <div class="mb-3">
                <label class="form-label fw-semibold">🎉 Événement</label>
                <input type="text" name="evenement"
                       class="form-control"
                       placeholder="Ex : Anniversaire, mariage, Noël…"
                       required
                       value="<%= (evenement != null ? evenement : "") %>">
            </div>

            <div class="mb-4">
                <label class="form-label fw-semibold">📅 Date d’expiration</label>
                <input type="date" name="expirationDate"
                       class="form-control"
                       required
                       value="<%= (expirationDate != null ? expirationDate : "") %>">
            </div>

            <div class="d-flex flex-column gap-2">
                <button type="submit" class="btn btn-primary btn-lg w-100 fw-bold">
                    🎁 Créer la liste
                </button>

                <a href="<%= request.getContextPath() %>/home" 
                   class="btn btn-secondary btn-lg w-100 fw-bold">
                    ❌ Annuler
                </a>
            </div>

        </form>

    </div>

</div>

</body>
</html>
