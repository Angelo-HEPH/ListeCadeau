<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, be.couderiannello.models.ListeCadeau, be.couderiannello.models.Cadeau, be.couderiannello.models.Personne" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Détail liste</title>
</head>

<body class="bg-light">

    <jsp:include page="/WEB-INF/view/includes/header.jsp" />

    <div class="container my-5">

        <%
            ListeCadeau liste = (ListeCadeau) request.getAttribute("liste");
            String error = (String) request.getAttribute("error");
        %>

        <div class="bg-white shadow-lg rounded-4 p-4 mb-4">
            <h1 class="fw-bold">📋 Détail : <%= liste.getTitle() %></h1>
            <p class="text-muted">
                Événement : 🎉 <%= liste.getEvenement() %> | Créée le : <%= liste.getCreationDate() %> | Expire le : <%= liste.getExpirationDate() %>
            </p>

            <% if (error != null && !error.isBlank()) { %>
                <div class="alert alert-danger text-center fw-bold">
                    <%= error %>
                </div>
            <% } %>

            <div class="mb-3">
                <a href="<%= request.getContextPath() %>/liste/edit?id=<%= liste.getId() %>"
                   class="btn btn-warning fw-bold">
                    ✏️ Modifier la liste
                </a>
                <a href="#" 
                   class="btn btn-primary ms-2 fw-bold"
                   onclick="copyShareLink('<%= liste.getShareLink() %>'); return false;">
                    🔗 Copier le lien
                </a>

                <script>
                    function copyShareLink(link) {
                        navigator.clipboard.writeText(link).then(() => {
                            alert('Lien de partage copié !');
                        }).catch(err => {
                            alert('Erreur lors de la copie du lien : ' + err);
                        });
                    }
                </script>
            </div>
        </div>

        <div class="bg-white shadow-sm rounded-4 p-4 mb-4">
            <h3 class="mb-3">Inviter une personne</h3>
            <form method="post" action="<%= request.getContextPath() %>/liste/manage" class="mb-4">
                <input type="hidden" name="action" value="invite">
                <input type="hidden" name="listeId" value="<%= liste.getId() %>">

                <div class="mb-3">
                    <label for="email" class="form-label fw-semibold">✉️ Email</label>
                    <input type="email" id="email" name="email" class="form-control"
                           placeholder="ex: quelquun@mail.com" required>
                </div>

                <button type="submit" class="btn btn-warning fw-bold">
                    Inviter
                </button>
            </form>

            <h3 class="mb-3">Invités</h3>
            <%
                List<Personne> invites = liste.getInvites();
                if (invites == null || invites.isEmpty()) {
            %>
                <p class="text-muted">Aucun invité pour le moment.</p>
            <%
                } else {
            %>
                <ul class="list-group">
                    <%
                        for (Personne p : invites) {
                    %>
                        <li class="list-group-item d-flex justify-content-between align-items-center">
                            <span>
                                <b><%= p.getFirstName() %> <%= p.getName() %></b>
                                <% if (p.getEmail() != null) { %> — <%= p.getEmail() %> <% } %>
                            </span>
                            <form method="post" action="<%= request.getContextPath() %>/liste/manage" style="margin:0;">
                                <input type="hidden" name="action" value="remove">
                                <input type="hidden" name="listeId" value="<%= liste.getId() %>">
                                <input type="hidden" name="personneId" value="<%= p.getId() %>">
                                <button type="submit" class="btn btn-danger btn-sm fw-bold"
                                        onclick="return confirm('Supprimer cet invité ?');">
                                    Supprimer
                                </button>
                            </form>
                        </li>
                    <%
                        }
                    %>
                </ul>
            <%
                }
            %>
        </div>

        <div class="bg-white shadow-sm rounded-4 p-4 mb-5">
            <h3 class="mb-4">🎁 Cadeaux</h3>
            <%
                List<Cadeau> cadeaux = liste.getCadeaux();
                if (cadeaux == null || cadeaux.isEmpty()) {
            %>
                <p class="text-muted">Aucun cadeau dans cette liste.</p>
            <%
                } else {
            %>
                <div class="row g-4">
                    <%
                        for (Cadeau c : cadeaux) {
                    %>
                        <div class="col-md-4">
                            <div class="card h-100 shadow-sm rounded-4">
                                <img src="<%= c.getPhoto() %>" class="card-img-top" 
                                     alt="photo cadeau" style="height:180px; object-fit:cover;">

                                <div class="card-body d-flex flex-column">
                                    <h5 class="card-title fw-bold"><%= c.getName() %></h5>
                                    <p class="card-text text-muted">
                                        <b>Prix :</b> <%= c.getPrice() %> €<br>
                                        <b>Priorité :</b> <%= c.getPriorite() %><br>
                                        <b>Description :</b> <%= c.getDescription() %>
                                    </p>

                                    <div class="mt-auto d-grid gap-2">
                                        <a href="<%= c.getLinkSite() %>" target="_blank" class="btn btn-primary btn-sm fw-bold">
                                            Voir le produit
                                        </a>
                                        <a href="<%= request.getContextPath() %>/cadeau/edit?id=<%= c.getId() %>&listeId=<%= liste.getId() %>"
                                           class="btn btn-warning btn-sm fw-bold">
                                            Modifier
                                        </a>
                                        <form action="<%= request.getContextPath() %>/cadeau/delete"
                                              method="post" onsubmit="return confirm('Supprimer ce cadeau ?');">
                                            <input type="hidden" name="cadeauId" value="<%= c.getId() %>">
                                            <input type="hidden" name="listeId" value="<%= liste.getId() %>">
                                            <button type="submit" class="btn btn-danger btn-sm fw-bold">
                                                Supprimer
                                            </button>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    <%
                        }
                    %>
                </div>
            <%
                }
            %>
        </div>

        <div class="mb-5 text-center">
            <a href="<%= request.getContextPath() %>/cadeau/create?listeId=<%= liste.getId() %>" 
               class="btn btn-primary btn-lg fw-bold">
                ➕ Ajouter un cadeau
            </a>
            <a href="<%= request.getContextPath() %>/liste/all" class="btn btn-secondary btn-lg fw-bold ms-2">
                ← Retour aux listes
            </a>
        </div>

    </div>

</body>
</html>
