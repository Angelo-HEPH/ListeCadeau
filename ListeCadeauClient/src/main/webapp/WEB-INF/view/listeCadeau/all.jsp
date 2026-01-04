<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, be.couderiannello.models.ListeCadeau" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Mes listes de cadeaux</title>
</head>

<body class="bg-light">

<jsp:include page="/WEB-INF/view/includes/header.jsp" />

<div class="container my-5">

    <div class="text-center mb-4">
        <h1 class="fw-bold">📋 Mes listes de cadeaux</h1>
        <p class="text-muted">
            Gérez vos listes existantes et accédez aux détails
        </p>
    </div>

<%
    String error = (String) request.getAttribute("error");
    if (error != null && !error.isBlank()) {
%>
    <div class="alert alert-danger fw-bold text-center">
        <%= error %>
    </div>
<%
    }
%>

<%
    List<ListeCadeau> listes = (List<ListeCadeau>) request.getAttribute("listes");

    if (listes == null || listes.isEmpty()) {
%>
    <div class="text-center text-muted mt-5">
        <p>Aucune liste pour le moment.</p>
        <a href="<%= request.getContextPath() %>/liste/create" class="btn btn-primary mt-2">
            ➕ Créer une liste
        </a>
    </div>
<%
    } else {
%>

    <div class="row g-4">

<%
        for (ListeCadeau l : listes) {
%>
        <div class="col-md-6 col-lg-4">
            <div class="card h-100 shadow-sm rounded-4">
                <div class="card-body d-flex flex-column justify-content-between">

                    <div>
                        <h5 class="card-title fw-bold">🎁 <%= l.getTitle() %></h5>
                        <p class="card-text text-muted">
                            🎉 <%= l.getEvenement() %>
                        </p>
                    </div>

                    <div class="mt-3 d-flex justify-content-between">
                        <a href="<%= request.getContextPath() %>/liste/manage?id=<%= l.getId() %>"
                           class="btn btn-primary btn-sm fw-bold">
                            Voir la liste
                        </a>

                        <form action="<%= request.getContextPath() %>/liste/delete"
                              method="post"
                              onsubmit="return confirm('Supprimer cette liste ?');">
                            <input type="hidden" name="id" value="<%= l.getId() %>" />
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

 	<div class="mt-4 text-center">
        <a class="btn btn-secondary btn-lg fw-bold" href="<%= request.getContextPath() %>/home">
            ← Retour à l’accueil
        </a>
    </div>

</div>

</body>
</html>
s