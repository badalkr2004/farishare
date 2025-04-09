<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Payments - FairShareBU</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.0/css/all.min.css">
    <style>
        body {
            background-color: #f8f9fa;
        }
        .sidebar {
            height: 100vh;
            background-color: #3498db;
            color: white;
            position: fixed;
            padding-top: 20px;
        }
        .sidebar .nav-link {
            color: rgba(255, 255, 255, 0.8);
            margin-bottom: 10px;
        }
        .sidebar .nav-link:hover {
            color: white;
        }
        .sidebar .nav-link.active {
            color: white;
            font-weight: bold;
        }
        .sidebar .nav-link i {
            margin-right: 10px;
        }
        .logo {
            padding: 15px;
            margin-bottom: 30px;
            text-align: center;
        }
        .logo h3 {
            color: white;
        }
        .content {
            margin-left: 240px;
            padding: 20px;
        }
        .card {
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }
        .payment-item {
            padding: 15px;
            border-bottom: 1px solid #eee;
        }
        .payment-item:last-child {
            border-bottom: none;
        }
        .payment-amount {
            font-weight: bold;
        }
        .payment-date {
            color: #6c757d;
            font-size: 0.9rem;
        }
        .badge-completed {
            background-color: #28a745;
        }
        .badge-pending {
            background-color: #ffc107;
        }
    </style>
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar -->
            <div class="col-md-2 sidebar">
                <div class="logo">
                    <h3>FairShareBU</h3>
                </div>
                <ul class="nav flex-column">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/dashboard"><i class="fas fa-home"></i> Dashboard</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/groups"><i class="fas fa-users"></i> Groups</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/expenses"><i class="fas fa-money-bill-wave"></i> Expenses</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/payments"><i class="fas fa-exchange-alt"></i> Payments</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/notifications">
                            <i class="fas fa-bell"></i> Notifications
                            <c:if test="${unreadNotifications > 0}">
                                <span class="badge bg-danger rounded-pill">${unreadNotifications}</span>
                            </c:if>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/profile"><i class="fas fa-user"></i> Profile</a>
                    </li>
                    <li class="nav-item mt-5">
                        <a class="nav-link" href="${pageContext.request.contextPath}/auth/logout"><i class="fas fa-sign-out-alt"></i> Logout</a>
                    </li>
                </ul>
            </div>
            
            <!-- Main Content -->
            <div class="col-md-10 content">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h2>Payment History</h2>
                    <a href="${pageContext.request.contextPath}/payments/create" class="btn btn-primary">
                        <i class="fas fa-plus"></i> Make a Payment
                    </a>
                </div>
                
                <div class="card">
                    <div class="card-header">
                        <ul class="nav nav-tabs card-header-tabs">
                            <li class="nav-item">
                                <a class="nav-link active" href="#all-payments" data-bs-toggle="tab">All Payments</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="#sent-payments" data-bs-toggle="tab">Payments Sent</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="#received-payments" data-bs-toggle="tab">Payments Received</a>
                            </li>
                        </ul>
                    </div>
                    <div class="card-body">
                        <div class="tab-content">
                            <!-- All Payments -->
                            <div class="tab-pane fade show active" id="all-payments">
                                <c:choose>
                                    <c:when test="${not empty payments}">
                                        <div class="list-group">
                                            <c:forEach items="${payments}" var="payment">
                                                <div class="payment-item">
                                                    <div class="row">
                                                        <div class="col-md-6">
                                                            <h5>
                                                                <c:choose>
                                                                    <c:when test="${payment.payer.userId eq user.userId}">
                                                                        You paid ${payment.receiver.fullName}
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        ${payment.payer.fullName} paid you
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </h5>
                                                            <p class="mb-1">${payment.description}</p>
                                                            <p class="payment-date">
                                                                <fmt:formatDate value="${payment.paymentDate}" pattern="MMM dd, yyyy HH:mm" />
                                                            </p>
                                                        </div>
                                                        <div class="col-md-3">
                                                            <span class="badge ${payment.status eq 'COMPLETED' ? 'bg-success' : 'bg-warning'}">
                                                                ${payment.status}
                                                            </span>
                                                            <p>Group: ${payment.group.name}</p>
                                                        </div>
                                                        <div class="col-md-3 text-end">
                                                            <h4 class="payment-amount">₹${payment.amount}</h4>
                                                        </div>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="text-center py-5">
                                            <i class="fas fa-exchange-alt fa-4x text-muted mb-3"></i>
                                            <h4>No payment history</h4>
                                            <p>Payments you make or receive will appear here.</p>
                                            <a href="${pageContext.request.contextPath}/payments/create" class="btn btn-primary mt-2">
                                                Make your first payment
                                            </a>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            
                            <!-- Payments Sent -->
                            <div class="tab-pane fade" id="sent-payments">
                                <c:choose>
                                    <c:when test="${not empty sentPayments}">
                                        <div class="list-group">
                                            <c:forEach items="${sentPayments}" var="payment">
                                                <div class="payment-item">
                                                    <div class="row">
                                                        <div class="col-md-6">
                                                            <h5>You paid ${payment.receiver.fullName}</h5>
                                                            <p class="mb-1">${payment.description}</p>
                                                            <p class="payment-date">
                                                                <fmt:formatDate value="${payment.paymentDate}" pattern="MMM dd, yyyy HH:mm" />
                                                            </p>
                                                        </div>
                                                        <div class="col-md-3">
                                                            <span class="badge ${payment.status eq 'COMPLETED' ? 'bg-success' : 'bg-warning'}">
                                                                ${payment.status}
                                                            </span>
                                                            <p>Group: ${payment.group.name}</p>
                                                        </div>
                                                        <div class="col-md-3 text-end">
                                                            <h4 class="payment-amount">₹${payment.amount}</h4>
                                                        </div>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="text-center py-5">
                                            <i class="fas fa-paper-plane fa-4x text-muted mb-3"></i>
                                            <h4>No payments sent</h4>
                                            <p>You haven't made any payments yet.</p>
                                            <a href="${pageContext.request.contextPath}/payments/create" class="btn btn-primary mt-2">
                                                Make a payment
                                            </a>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            
                            <!-- Payments Received -->
                            <div class="tab-pane fade" id="received-payments">
                                <c:choose>
                                    <c:when test="${not empty receivedPayments}">
                                        <div class="list-group">
                                            <c:forEach items="${receivedPayments}" var="payment">
                                                <div class="payment-item">
                                                    <div class="row">
                                                        <div class="col-md-6">
                                                            <h5>${payment.payer.fullName} paid you</h5>
                                                            <p class="mb-1">${payment.description}</p>
                                                            <p class="payment-date">
                                                                <fmt:formatDate value="${payment.paymentDate}" pattern="MMM dd, yyyy HH:mm" />
                                                            </p>
                                                        </div>
                                                        <div class="col-md-3">
                                                            <span class="badge ${payment.status eq 'COMPLETED' ? 'bg-success' : 'bg-warning'}">
                                                                ${payment.status}
                                                            </span>
                                                            <p>Group: ${payment.group.name}</p>
                                                        </div>
                                                        <div class="col-md-3 text-end">
                                                            <h4 class="payment-amount">₹${payment.amount}</h4>
                                                        </div>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="text-center py-5">
                                            <i class="fas fa-hand-holding-usd fa-4x text-muted mb-3"></i>
                                            <h4>No payments received</h4>
                                            <p>You haven't received any payments yet.</p>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 