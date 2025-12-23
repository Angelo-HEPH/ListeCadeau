<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, be.couderiannello.models.ListeCadeau" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Mes listes</title>
</head>
<body>

<jsp:include page="/WEB-INF/view/includes/header.jsp" />

<div class="container mt-4">

    <h2 class="mb-4">Mes listes de cadeaux</h2>

    <%
        List<ListeCadeau> listes = (List<ListeCadeau>) request.getAttribute("listes");

        if (listes == null || listes.isEmpty()) {
    %>
        <p class="text-muted">Aucune liste pour le moment.</p>

    <%
        } else {
            for (ListeCadeau l : listes) {
    %>

        <div class="card mb-3 shadow-sm">
            <div class="card-body">
                <h5 class="card-title"><%= l.getTitle() %></h5>
                <p class="card-text text-muted">
                    <%= l.getEvenement() %>
                </p>

                <a href="<%= request.getContextPath() %>/liste/manage?id=<%= l.getId() %>"
                   class="btn btn-primary btn-sm">
                    Voir la liste
                </a>
            </div>
        </div>

    <%
            }
        }
    %>
    
</div>

</body>
</html>
