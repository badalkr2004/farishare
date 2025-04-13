<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<header class="navbar navbar-expand-lg navbar-dark bg-primary">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard.jsp">FairShareBU</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav"
            aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto">
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/dashboard.jsp">Dashboard</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/groups/">Groups</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/expenses/">Expenses</a>
                </li>
            </ul>
            <c:if test="${not empty sessionScope.user}">
                <ul class="navbar-nav">
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button"
                            data-bs-toggle="dropdown" aria-expanded="false">
                            <c:if test="${empty sessionScope.user.profilePicture}">
                                <i class="fas fa-user-circle"></i>
                            </c:if>
                            <c:if test="${not empty sessionScope.user.profilePicture}">
                                <img src="${pageContext.request.contextPath}/uploads/profiles/${sessionScope.user.profilePicture}" 
                                    alt="Profile" class="avatar-xs rounded-circle me-1">
                            </c:if>
                            ${sessionScope.user.username}
                        </a>
                        <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userDropdown">
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/profile">Profile</a></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/notifications/">Notifications</a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout">Logout</a></li>
                        </ul>
                    </li>
                </ul>
            </c:if>
            <c:if test="${empty sessionScope.user}">
                <div class="d-flex">
                    <a href="${pageContext.request.contextPath}/login.jsp" class="btn btn-outline-light me-2">Login</a>
                    <a href="${pageContext.request.contextPath}/register.jsp" class="btn btn-light">Register</a>
                </div>
            </c:if>
        </div>
    </div>
</header> 