<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Payment Details - FairShareBU</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.0/css/all.min.css">
    <style>
        body {
            background-color: #f8f9fa;
        }
        .payment-card {
            max-width: 800px;
            margin: 0 auto;
            padding: 30px;
            background-color: #fff;
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }
        .payment-header {
            border-bottom: 1px solid #eee;
            padding-bottom: 20px;
            margin-bottom: 20px;
        }
        .payment-amount {
            font-size: 2.5rem;
            font-weight: bold;
            color: #3498db;
        }
        .payment-status {
            display: inline-block;
            padding: 5px 10px;
            border-radius: 30px;
            font-size: 0.9rem;
        }
        .status-completed {
            background-color: #e8f6ee;
            color: #28a745;
        }
        .status-pending {
            background-color: #fff8e6;
            color: #ffc107;
        }
        .status-cancelled {
            background-color: #feeceb;
            color: #dc3545;
        }
        .payment-info {
            margin-bottom: 30px;
        }
        .payment-info .row {
            margin-bottom: 15px;
        }
        .payment-label {
            font-weight: 600;
            color: #6c757d;
        }
        .actions-container {
            margin-top: 30px;
            padding-top: 20px;
            border-top: 1px solid #eee;
        }
    </style>
</head>
<body>
    <div class="container mt-5 mb-5">
        <div class="payment-card">
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger mb-4">${errorMessage}</div>
            </c:if>
            
            <div class="payment-header">
                <div class="d-flex justify-content-between align-items-center">
                    <h2>Payment Details</h2>
                    <div>
                        <span class="payment-status ${payment.status eq 'COMPLETED' ? 'status-completed' : payment.status eq 'PENDING' ? 'status-pending' : 'status-cancelled'}">
                            ${payment.status}
                        </span>
                    </div>
                </div>
                <div class="text-center mt-4">
                    <div class="payment-amount">₹${payment.amount}</div>
                    <p class="text-muted mt-2">${payment.description}</p>
                </div>
            </div>
            
            <div class="payment-info">
                <div class="row">
                    <div class="col-md-4 payment-label">From</div>
                    <div class="col-md-8">${payment.payer.fullName}</div>
                </div>
                <div class="row">
                    <div class="col-md-4 payment-label">To</div>
                    <div class="col-md-8">${payment.receiver.fullName}</div>
                </div>
                <div class="row">
                    <div class="col-md-4 payment-label">Group</div>
                    <div class="col-md-8">${payment.group.name}</div>
                </div>
                <div class="row">
                    <div class="col-md-4 payment-label">Date</div>
                    <div class="col-md-8">
                        <fmt:formatDate value="${payment.paymentDate}" pattern="MMMM dd, yyyy HH:mm" />
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-4 payment-label">Transaction ID</div>
                    <div class="col-md-8">#${payment.paymentId}</div>
                </div>
            </div>
            
            <div class="actions-container">
                <div class="d-flex justify-content-between">
                    <a href="${pageContext.request.contextPath}/payments" class="btn btn-outline-secondary">
                        <i class="fas fa-arrow-left"></i> Back to Payments
                    </a>
                    
                    <c:if test="${payment.status eq 'PENDING' && payment.payer.userId eq user.userId}">
                        <form action="${pageContext.request.contextPath}/payments" method="post" class="d-inline">
                            <input type="hidden" name="action" value="cancel">
                            <input type="hidden" name="id" value="${payment.paymentId}">
                            <button type="submit" class="btn btn-danger" onclick="return confirm('Are you sure you want to cancel this payment?')">
                                <i class="fas fa-times"></i> Cancel Payment
                            </button>
                        </form>
                    </c:if>
                    
                    <a href="${pageContext.request.contextPath}/expenses/group?id=${payment.group.groupId}" class="btn btn-primary">
                        <i class="fas fa-users"></i> View Group Expenses
                    </a>
                </div>
            </div>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 