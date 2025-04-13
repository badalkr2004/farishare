<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Select Settlement | FairShare</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome for icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.0/css/all.min.css">
    <style>
        body {
            background-color: #f8f9fa;
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }
        .content {
            flex: 1 0 auto;
            padding: 20px 0;
        }
        .card {
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            border-radius: 10px;
            overflow: hidden;
        }
        .card-header {
            background-color: #3498db;
            color: white;
            font-weight: 500;
        }
        .avatar-sm {
            width: 30px;
            height: 30px;
            border-radius: 50%;
            object-fit: cover;
        }
        .text-success {
            color: #28a745 !important;
        }
        .text-danger {
            color: #dc3545 !important;
        }
        .btn-primary {
            background-color: #3498db;
            border-color: #3498db;
        }
        .btn-success {
            background-color: #28a745;
            border-color: #28a745;
        }
        .btn-primary:hover, .btn-success:hover {
            filter: brightness(90%);
        }
        .member-card {
            transition: all 0.2s ease;
        }
        .member-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }
    </style>
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar -->
            <div class="col-md-2 sidebar" style="background-color: #3498db; color: white; position: fixed; padding-top: 20px; height: 100vh;">
                <div class="logo" style="padding: 15px; margin-bottom: 30px; text-align: center;">
                    <h3 style="color: white;">FairShareBU</h3>
                </div>
                <ul class="nav flex-column">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/dashboard" style="color: rgba(255, 255, 255, 0.8); margin-bottom: 10px;"><i class="fas fa-home me-2"></i> Dashboard</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/groups" style="color: rgba(255, 255, 255, 0.8); margin-bottom: 10px;"><i class="fas fa-users me-2"></i> Groups</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/expenses" style="color: white; font-weight: bold; margin-bottom: 10px;"><i class="fas fa-money-bill-wave me-2"></i> Expenses</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/payments" style="color: rgba(255, 255, 255, 0.8); margin-bottom: 10px;"><i class="fas fa-exchange-alt me-2"></i> Payments</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/notifications" style="color: rgba(255, 255, 255, 0.8); margin-bottom: 10px;">
                            <i class="fas fa-bell me-2"></i> Notifications
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/profile" style="color: rgba(255, 255, 255, 0.8); margin-bottom: 10px;"><i class="fas fa-user me-2"></i> Profile</a>
                    </li>
                    <li class="nav-item mt-5">
                        <a class="nav-link" href="${pageContext.request.contextPath}/auth/logout" style="color: rgba(255, 255, 255, 0.8); margin-bottom: 10px;"><i class="fas fa-sign-out-alt me-2"></i> Logout</a>
                    </li>
                </ul>
            </div>
            
            <!-- Main Content -->
            <div class="col-md-10 content" style="margin-left: 240px; padding: 20px;">
                <div class="mt-4">
                    <div class="row">
                        <div class="col-md-12">
                            <div class="card">
                                <div class="card-header">
                                    <h4><i class="fas fa-exchange-alt me-2"></i>Select Users for Settlement - ${group.name}</h4>
                                </div>
                                <div class="card-body">
                                    <div class="alert alert-info">
                                        <i class="fas fa-info-circle me-2"></i> Select who should pay (debtor) and who should receive the payment (creditor) to settle balances.
                                    </div>
                                    
                                    <div class="table-responsive">
                                        <table class="table table-hover">
                                            <thead class="table-light">
                                                <tr>
                                                    <th>Member</th>
                                                    <th>Balance</th>
                                                    <th>Actions</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="balance" items="${balances}">
                                                    <tr>
                                                        <td>
                                                            <div class="d-flex align-items-center">
                                                                <c:if test="${empty balance.user.profilePicture}">
                                                                    <div class="bg-secondary text-white rounded-circle d-flex align-items-center justify-content-center me-2" style="width: 30px; height: 30px;">
                                                                        <i class="fas fa-user-circle"></i>
                                                                    </div>
                                                                </c:if>
                                                                <c:if test="${not empty balance.user.profilePicture}">
                                                                    <img src="${pageContext.request.contextPath}/uploads/profiles/${balance.user.profilePicture}" alt="Profile" class="avatar-sm me-2">
                                                                </c:if>
                                                                <span>${balance.user.fullName}</span>
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${balance.amount > 0}">
                                                                    <span class="text-success fw-bold">
                                                                        <i class="fas fa-plus-circle me-1"></i>₹<fmt:formatNumber value="${balance.amount}" minFractionDigits="2" maxFractionDigits="2"/>
                                                                    </span>
                                                                </c:when>
                                                                <c:when test="${balance.amount < 0}">
                                                                    <span class="text-danger fw-bold">
                                                                        <i class="fas fa-minus-circle me-1"></i>₹<fmt:formatNumber value="${balance.amount * -1}" minFractionDigits="2" maxFractionDigits="2"/>
                                                                    </span>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span class="text-secondary">₹0.00</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td>
                                                            <c:if test="${balance.amount < 0}">
                                                                <button onclick="selectPayer('${balance.user.userId}')" class="btn btn-primary btn-sm">
                                                                    <i class="fas fa-user-minus me-1"></i>Select as Payer
                                                                </button>
                                                            </c:if>
                                                            <c:if test="${balance.amount > 0}">
                                                                <button onclick="selectReceiver('${balance.user.userId}')" class="btn btn-success btn-sm">
                                                                    <i class="fas fa-user-plus me-1"></i>Select as Receiver
                                                                </button>
                                                            </c:if>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                    
                                    <c:if test="${not empty param.payerId}">
                                        <div class="mt-4">
                                            <h5 class="border-bottom pb-2 mb-3"><i class="fas fa-hand-holding-usd me-2"></i>Select who should receive payment:</h5>
                                            <div class="row">
                                                <c:forEach var="balance" items="${balances}">
                                                    <c:if test="${balance.amount > 0}">
                                                        <div class="col-md-4 mb-3">
                                                            <div class="card member-card h-100">
                                                                <div class="card-body">
                                                                    <div class="d-flex align-items-center mb-3">
                                                                        <c:if test="${empty balance.user.profilePicture}">
                                                                            <div class="bg-secondary text-white rounded-circle d-flex align-items-center justify-content-center me-2" style="width: 36px; height: 36px;">
                                                                                <i class="fas fa-user-circle"></i>
                                                                            </div>
                                                                        </c:if>
                                                                        <c:if test="${not empty balance.user.profilePicture}">
                                                                            <img src="${pageContext.request.contextPath}/uploads/profiles/${balance.user.profilePicture}" alt="Profile" class="avatar-sm me-2" style="width: 36px; height: 36px;">
                                                                        </c:if>
                                                                        <h5 class="card-title mb-0">${balance.user.fullName}</h5>
                                                                    </div>
                                                                    <p class="card-text text-success mb-3">
                                                                        <i class="fas fa-plus-circle me-1"></i>Owed: ₹<fmt:formatNumber value="${balance.amount}" minFractionDigits="2" maxFractionDigits="2"/>
                                                                    </p>
                                                                    <button onclick="proceedToSettlement('${param.payerId}', '${balance.user.userId}')" class="btn btn-primary w-100">
                                                                        <i class="fas fa-check-circle me-1"></i>Select
                                                                    </button>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </c:if>
                                                </c:forEach>
                                            </div>
                                        </div>
                                    </c:if>
                                    
                                    <c:if test="${not empty param.receiverId}">
                                        <div class="mt-4">
                                            <h5 class="border-bottom pb-2 mb-3"><i class="fas fa-money-bill-wave me-2"></i>Select who should pay:</h5>
                                            <div class="row">
                                                <c:forEach var="balance" items="${balances}">
                                                    <c:if test="${balance.amount < 0}">
                                                        <div class="col-md-4 mb-3">
                                                            <div class="card member-card h-100">
                                                                <div class="card-body">
                                                                    <div class="d-flex align-items-center mb-3">
                                                                        <c:if test="${empty balance.user.profilePicture}">
                                                                            <div class="bg-secondary text-white rounded-circle d-flex align-items-center justify-content-center me-2" style="width: 36px; height: 36px;">
                                                                                <i class="fas fa-user-circle"></i>
                                                                            </div>
                                                                        </c:if>
                                                                        <c:if test="${not empty balance.user.profilePicture}">
                                                                            <img src="${pageContext.request.contextPath}/uploads/profiles/${balance.user.profilePicture}" alt="Profile" class="avatar-sm me-2" style="width: 36px; height: 36px;">
                                                                        </c:if>
                                                                        <h5 class="card-title mb-0">${balance.user.fullName}</h5>
                                                                    </div>
                                                                    <p class="card-text text-danger mb-3">
                                                                        <i class="fas fa-minus-circle me-1"></i>Owes: ₹<fmt:formatNumber value="${balance.amount * -1}" minFractionDigits="2" maxFractionDigits="2"/>
                                                                    </p>
                                                                    <button onclick="proceedToSettlement('${balance.user.userId}', '${param.receiverId}')" class="btn btn-primary w-100">
                                                                        <i class="fas fa-check-circle me-1"></i>Select
                                                                    </button>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </c:if>
                                                </c:forEach>
                                            </div>
                                        </div>
                                    </c:if>
                                    
                                    <div class="mt-4">
                                        <a href="${pageContext.request.contextPath}/expenses/group?id=${group.groupId}" class="btn btn-secondary">
                                            <i class="fas fa-arrow-left me-1"></i>Back to Group
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function selectPayer(payerId) {
            window.location.href = "${pageContext.request.contextPath}/expenses/settle?groupId=${group.groupId}&payerId=" + payerId;
        }
        
        function selectReceiver(receiverId) {
            window.location.href = "${pageContext.request.contextPath}/expenses/settle?groupId=${group.groupId}&receiverId=" + receiverId;
        }
        
        function proceedToSettlement(payerId, receiverId) {
            window.location.href = "${pageContext.request.contextPath}/expenses/settle?groupId=${group.groupId}&payerId=" + payerId + "&receiverId=" + receiverId;
        }
    </script>
</body>
</html> 