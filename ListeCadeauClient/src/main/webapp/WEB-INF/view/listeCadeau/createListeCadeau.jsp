<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Créer une liste de cadeaux</title>
</head>
<body>

<jsp:include page="/WEB-INF/view/includes/header.jsp" />

<div class="container mt-4">

    <h1 class="mb-4">Créer une liste de cadeaux</h1>

    <form action="" method="post" class="border rounded p-4 shadow-sm" style="max-width:500px;">

        <div class="mb-3">
            <label class="form-label">Titre :</label>
            <input type="text" name="title" class="form-control" required>
        </div>

        <div class="mb-3">
            <label class="form-label">Événement :</label>
            <input type="text" name="evenement" class="form-control" required>
        </div>

        <div class="mb-3">
            <label class="form-label">Date d'expiration :</label>
            <input type="date" name="expirationDate" class="form-control" required>
        </div>

        <div class="mb-3">
            <label class="form-label">Lien de partage :</label>
            <input type="text" name="shareLink" value="https://" class="form-control">
        </div>

        <button type="submit" class="btn btn-primary w-100">
            Créer
        </button>
    </form>

    <% 
        String error = (String) request.getAttribute("error");
        if (error != null) {
    %>
        <p class="text-danger mt-3 fw-bold"><%= error %></p>
    <% } %>

</div>

</body>
</html>
