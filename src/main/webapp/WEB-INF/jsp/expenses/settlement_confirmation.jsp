<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Settlement Confirmation | FairShare</title>
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
            width: 32px;
            height: 32px;
            border-radius: 50%;
            object-fit: cover;
        }
        .avatar-md {
            width: 48px;
            height: 48px;
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
        .settlement-details {
            border-radius: 10px;
            background-color: rgba(248, 249, 250, 0.8);
            border: 1px solid #dee2e6;
        }
        .settlement-icon {
            font-size: 2.5rem;
            color: #3498db;
        }
        .arrow-icon {
            font-size: 1.5rem;
            color: #6c757d;
        }
    </style>
</head>
<body>
    <jsp:include page="../includes/header.jsp" />
    
    <div class="content">
        <div class="container mt-4">
            <div class="row">
                <div class="col-lg-8 offset-lg-2">
                    <div class="card">
                        <div class="card-header">
                            <h4><i class="fas fa-handshake me-2"></i>Settlement Confirmation</h4>
                        </div>
                        <div class="card-body">
                            <div class="alert alert-info mb-4">
                                <i class="fas fa-info-circle me-2"></i> Please confirm the settlement details below. Once confirmed, a new expense will be created to record this settlement.
                            </div>
                            
                            <div class="settlement-details p-4 mb-4">
                                <div class="text-center mb-4">
                                    <i class="fas fa-exchange-alt settlement-icon mb-3"></i>
                                    <h4 class="fw-bold">Settlement Details</h4>
                                    <p class="text-muted">Group: ${group.name}</p>
                                </div>
                                
                                <div class="row align-items-center justify-content-center">
                                    <div class="col-md-4 text-center">
                                        <div class="mb-2">
                                            <c:if test="${empty payer.profilePicture}">
                                                <div class="bg-secondary text-white rounded-circle d-flex align-items-center justify-content-center mx-auto mb-2" style="width: 64px; height: 64px;">
                                                    <i class="fas fa-user-circle fa-2x"></i>
                                                </div>
                                            </c:if>
                                            <c:if test="${not empty payer.profilePicture}">
                                                <img src="${pageContext.request.contextPath}/uploads/profiles/${payer.profilePicture}" alt="Payer" class="avatar-md mx-auto mb-2" style="width: 64px; height: 64px;">
                                            </c:if>
                                            <h5>${payer.fullName}</h5>
                                            <p class="badge bg-danger text-white">Pays</p>
                                        </div>
                                    </div>
                                    
                                    <div class="col-md-4 text-center">
                                        <div class="d-flex flex-column align-items-center">
                                            <i class="fas fa-long-arrow-alt-right arrow-icon mb-2"></i>
                                            <div class="bg-light p-3 rounded-3">
                                                <h4 class="text-primary fw-bold mb-0">
                                                    $<fmt:formatNumber value="${amount}" minFractionDigits="2" maxFractionDigits="2"/>
                                                </h4>
                                            </div>
                                        </div>
                                    </div>
                                    
                                    <div class="col-md-4 text-center">
                                        <div class="mb-2">
                                            <c:if test="${empty receiver.profilePicture}">
                                                <div class="bg-secondary text-white rounded-circle d-flex align-items-center justify-content-center mx-auto mb-2" style="width: 64px; height: 64px;">
                                                    <i class="fas fa-user-circle fa-2x"></i>
                                                </div>
                                            </c:if>
                                            <c:if test="${not empty receiver.profilePicture}">
                                                <img src="${pageContext.request.contextPath}/uploads/profiles/${receiver.profilePicture}" alt="Receiver" class="avatar-md mx-auto mb-2" style="width: 64px; height: 64px;">
                                            </c:if>
                                            <h5>${receiver.fullName}</h5>
                                            <p class="badge bg-success text-white">Receives</p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            
                            <form action="${pageContext.request.contextPath}/expenses" method="post" class="needs-validation" novalidate>
                                <input type="hidden" name="action" value="confirmSettle">
                                <input type="hidden" name="groupId" value="${group.groupId}">
                                <input type="hidden" name="payerId" value="${payer.userId}">
                                <input type="hidden" name="receiverId" value="${receiver.userId}">
                                <input type="hidden" name="amount" value="${amount}">
                                
                                <div class="mb-3">
                                    <label for="description" class="form-label">Description</label>
                                    <div class="input-group">
                                        <span class="input-group-text"><i class="fas fa-pen"></i></span>
                                        <input type="text" class="form-control" id="description" name="description" 
                                            value="Settlement: ${payer.fullName} paid ${receiver.fullName}" required>
                                    </div>
                                    <div class="invalid-feedback">
                                        Please provide a description for this settlement.
                                    </div>
                                </div>
                                
                                <div class="mb-3">
                                    <label for="paymentMethod" class="form-label">Payment Method</label>
                                    <div class="input-group">
                                        <span class="input-group-text"><i class="fas fa-credit-card"></i></span>
                                        <select class="form-select" id="paymentMethod" name="paymentMethod" required>
                                            <option value="CASH">Cash</option>
                                            <option value="BANK_TRANSFER">Bank Transfer</option>
                                            <option value="CREDIT_CARD">Credit Card</option>
                                            <option value="PAYPAL">PayPal</option>
                                            <option value="VENMO">Venmo</option>
                                            <option value="OTHER">Other</option>
                                        </select>
                                    </div>
                                    <div class="invalid-feedback">
                                        Please select a payment method.
                                    </div>
                                </div>
                                
                                <div class="d-flex justify-content-between mt-4">
                                    <a href="${pageContext.request.contextPath}/expenses?action=settle&groupId=${group.groupId}" class="btn btn-secondary">
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
    
    <jsp:include page="../includes/footer.jsp" />
    
    <!-- Bootstrap JS Bundle with Popper -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
    
    <script>
    // Form validation
    (function() {
        'use strict';
        
        const forms = document.querySelectorAll('.needs-validation');
        
        Array.from(forms).forEach(form => {
            form.addEventListener('submit', event => {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                
                form.classList.add('was-validated');
            }, false);
        });
    })();
    </script>
</body>
</html> 