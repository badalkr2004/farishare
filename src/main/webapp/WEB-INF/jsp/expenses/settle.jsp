<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Settle Up | FairShareBU</title>
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
            overflow: hidden;
        }
        .card-header {
            background-color: #3498db;
            color: white;
        }
        .avatar-sm {
            width: 30px;
            height: 30px;
            border-radius: 50%;
            background-color: #6c757d;
            color: white;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 14px;
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
                        <a class="nav-link active" href="${pageContext.request.contextPath}/expenses"><i class="fas fa-money-bill-wave"></i> Expenses</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/payments"><i class="fas fa-exchange-alt"></i> Payments</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/notifications">
                            <i class="fas fa-bell"></i> Notifications
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
                <div class="mt-4">
                    <div class="row">
                        <div class="col-md-8 offset-md-2">
                            <div class="card">
                                <div class="card-header">
                                    <h4><i class="fas fa-exchange-alt me-2"></i>Settle Up - ${group.name}</h4>
                                </div>
                                <div class="card-body">
                                    <form action="${pageContext.request.contextPath}/expenses" method="post">
                                        <input type="hidden" name="action" value="settle">
                                        <input type="hidden" name="groupId" value="${group.groupId}">
                                        <input type="hidden" name="payerId" value="${payer.userId}">
                                        <input type="hidden" name="receiverId" value="${receiver.userId}">
                                        
                                        <div class="mb-3">
                                            <label class="form-label fw-bold">From:</label>
                                            <div class="input-group">
                                                <span class="input-group-text">
                                                    <div class="avatar-sm">
                                                        <i class="fas fa-user"></i>
                                                    </div>
                                                </span>
                                                <input type="text" class="form-control" value="${payer.fullName}" readonly>
                                            </div>
                                        </div>
                                        
                                        <div class="mb-3">
                                            <label class="form-label fw-bold">To:</label>
                                            <div class="input-group">
                                                <span class="input-group-text">
                                                    <div class="avatar-sm">
                                                        <i class="fas fa-user"></i>
                                                    </div>
                                                </span>
                                                <input type="text" class="form-control" value="${receiver.fullName}" readonly>
                                            </div>
                                        </div>
                                        
                                        <div class="mb-3">
                                            <label for="amount" class="form-label fw-bold">Amount:</label>
                                            <div class="input-group">
                                                <span class="input-group-text">₹</span>
                                                <input type="number" step="0.01" min="0.01" id="amount" name="amount" class="form-control" value="<fmt:formatNumber value="${amount}" minFractionDigits="2" maxFractionDigits="2" pattern="0.00"/>" required>
                                            </div>
                                            <small class="text-muted">Total amount owed: ₹<fmt:formatNumber value="${amount}" minFractionDigits="2" maxFractionDigits="2"/></small>
                                        </div>
                                        
                                        <div class="mb-3">
                                            <label for="notes" class="form-label fw-bold">Notes (optional):</label>
                                            <textarea id="notes" name="notes" class="form-control" rows="3" placeholder="Add any notes about this settlement"></textarea>
                                        </div>
                                        
                                        <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                                            <a href="${pageContext.request.contextPath}/expenses/settle?groupId=${group.groupId}" class="btn btn-secondary me-md-2">
                                                <i class="fas fa-arrow-left me-1"></i>Back
                                            </a>
                                            <button type="submit" class="btn btn-success">
                                                <i class="fas fa-check-circle me-1"></i>Confirm Settlement
                                            </button>
                                        </div>
                                    </form>
                                </div>
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