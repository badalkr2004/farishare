<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Expense - FairShareBU</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.0/css/all.min.css">
    <style>
        body {
            background-color: #f8f9fa;
        }
        .expense-form {
            max-width: 800px;
            margin: 0 auto;
            padding: 30px;
            background-color: #fff;
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }
        .form-group {
            margin-bottom: 20px;
        }
        .btn-primary {
            background-color: #3498db;
            border-color: #3498db;
        }
        .btn-primary:hover {
            background-color: #2980b9;
            border-color: #2980b9;
        }
        .member-row {
            padding: 10px;
            border-bottom: 1px solid #eee;
        }
        .member-row:last-child {
            border-bottom: none;
        }
    </style>
</head>
<body>
    <div class="container mt-5">
        <div class="expense-form">
            <h2 class="mb-4 text-center">Create a New Expense</h2>
            
            <c:if test="${not empty error}">
                <div class="alert alert-danger">${error}</div>
            </c:if>
            
            <form action="${pageContext.request.contextPath}/expenses" method="post" enctype="multipart/form-data">
                <input type="hidden" name="action" value="create">
                
                <div class="form-group">
                    <label for="groupId">Group</label>
                    <select id="groupId" name="groupId" class="form-control" required onchange="loadGroupMembers()">
                        <option value="">Select a group</option>
                        <c:forEach items="${userGroups}" var="group">
                            <option value="${group.groupId}" ${param.groupId eq group.groupId ? 'selected' : ''}>${group.name}</option>
                        </c:forEach>
                    </select>
                </div>
                
                <div class="form-group">
                    <label for="description">Description</label>
                    <input type="text" id="description" name="description" class="form-control" required placeholder="What is this expense for?">
                </div>
                
                <div class="form-group">
                    <label for="amount">Amount</label>
                    <div class="input-group">
                        <span class="input-group-text">₹</span>
                        <input type="number" id="amount" name="amount" class="form-control" step="0.01" min="0.01" required onchange="updateEqualSplit()">
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="paymentMethod">Payment Method</label>
                    <select id="paymentMethod" name="paymentMethod" class="form-control">
                        <option value="CASH">Cash</option>
                        <option value="CREDIT_CARD">Credit Card</option>
                        <option value="DEBIT_CARD">Debit Card</option>
                        <option value="UPI">UPI</option>
                        <option value="BANK_TRANSFER">Bank Transfer</option>
                        <option value="OTHER">Other</option>
                    </select>
                </div>
                
                <div class="form-group">
                    <label for="receiptImage">Receipt (Optional)</label>
                    <input type="file" id="receiptImage" name="receiptImage" class="form-control" accept="image/*">
                </div>
                
                <hr>
                
                <div class="form-group">
                    <label>Split Method</label>
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="splitMethod" id="equalSplit" value="equal" checked onchange="toggleSplitMethod()">
                        <label class="form-check-label" for="equalSplit">
                            Split Equally
                        </label>
                    </div>
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="splitMethod" id="percentageSplit" value="percentage" onchange="toggleSplitMethod()">
                        <label class="form-check-label" for="percentageSplit">
                            Split by Percentage
                        </label>
                    </div>
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="splitMethod" id="amountSplit" value="amount" onchange="toggleSplitMethod()">
                        <label class="form-check-label" for="amountSplit">
                            Split by Amount
                        </label>
                    </div>
                </div>
                
                <div id="membersList" class="card mt-3 mb-3">
                    <div class="card-header">
                        <h5>Group Members</h5>
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${not empty members}">
                                <div id="equalSplitSection" class="split-section">
                                    <p class="text-muted">Each person will pay an equal share of the total amount.</p>
                                    <div class="member-list">
                                        <c:forEach items="${members}" var="member">
                                            <div class="member-row">
                                                <div class="form-check">
                                                    <input class="form-check-input member-checkbox" type="checkbox" 
                                                           id="equal_${member.userId}" 
                                                           name="member_${member.userId}" 
                                                           value="true" 
                                                           ${member.userId eq user.userId ? 'disabled checked' : 'checked'}
                                                           onchange="updateEqualSplit()">
                                                    <label class="form-check-label" for="equal_${member.userId}">
                                                        ${member.fullName} ${member.userId eq user.userId ? '(You)' : ''}
                                                    </label>
                                                    <span class="float-end equal-amount">₹0.00</span>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>
                                
                                <div id="percentageSplitSection" class="split-section" style="display: none;">
                                    <p class="text-muted">Specify the percentage each person will pay. Total must equal 100%.</p>
                                    <div class="member-list">
                                        <c:forEach items="${members}" var="member">
                                            <div class="member-row">
                                                <div class="row align-items-center">
                                                    <div class="col-md-6">
                                                        <label for="percent_${member.userId}">
                                                            ${member.fullName} ${member.userId eq user.userId ? '(You)' : ''}
                                                        </label>
                                                    </div>
                                                    <div class="col-md-6">
                                                        <div class="input-group">
                                                            <input type="number" class="form-control percentage-input" 
                                                                   id="percent_${member.userId}" 
                                                                   name="percent_${member.userId}" 
                                                                   min="0" max="100" step="0.01" 
                                                                   value="${member.userId eq user.userId ? '100' : '0'}"
                                                                   onchange="validatePercentages()">
                                                            <span class="input-group-text">%</span>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:forEach>
                                        <div class="mt-2 text-end">
                                            <span id="totalPercentage">Total: 100%</span>
                                        </div>
                                    </div>
                                </div>
                                
                                <div id="amountSplitSection" class="split-section" style="display: none;">
                                    <p class="text-muted">Specify the exact amount each person will pay. Total should equal the expense amount.</p>
                                    <div class="member-list">
                                        <c:forEach items="${members}" var="member">
                                            <div class="member-row">
                                                <div class="row align-items-center">
                                                    <div class="col-md-6">
                                                        <label for="amount_${member.userId}">
                                                            ${member.fullName} ${member.userId eq user.userId ? '(You)' : ''}
                                                        </label>
                                                    </div>
                                                    <div class="col-md-6">
                                                        <div class="input-group">
                                                            <span class="input-group-text">₹</span>
                                                            <input type="number" class="form-control amount-input" 
                                                                   id="amount_${member.userId}" 
                                                                   name="amount_${member.userId}" 
                                                                   min="0" step="0.01" 
                                                                   value="${member.userId eq user.userId ? amount : '0'}"
                                                                   onchange="validateAmounts()">
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:forEach>
                                        <div class="mt-2 text-end">
                                            <span id="totalAmount">Total: ₹0.00</span>
                                        </div>
                                    </div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <p class="text-muted">Please select a group to see members.</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                
                <div class="form-group">
                    <button type="submit" class="btn btn-primary w-100">Create Expense</button>
                </div>
                
                <div class="text-center mt-3">
                    <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-link">Cancel</a>
                </div>
            </form>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function loadGroupMembers() {
            const groupId = document.getElementById('groupId').value;
            if (groupId) {
                // Redirect to the same page with the groupId parameter
                window.location.href = '${pageContext.request.contextPath}/expenses/create?groupId=' + groupId;
            }
        }
        
        function toggleSplitMethod() {
            const equalSplit = document.getElementById('equalSplit');
            const percentageSplit = document.getElementById('percentageSplit');
            const amountSplit = document.getElementById('amountSplit');
            
            const equalSplitSection = document.getElementById('equalSplitSection');
            const percentageSplitSection = document.getElementById('percentageSplitSection');
            const amountSplitSection = document.getElementById('amountSplitSection');
            
            if (equalSplit.checked) {
                equalSplitSection.style.display = 'block';
                percentageSplitSection.style.display = 'none';
                amountSplitSection.style.display = 'none';
                updateEqualSplit();
            } else if (percentageSplit.checked) {
                equalSplitSection.style.display = 'none';
                percentageSplitSection.style.display = 'block';
                amountSplitSection.style.display = 'none';
                validatePercentages();
            } else if (amountSplit.checked) {
                equalSplitSection.style.display = 'none';
                percentageSplitSection.style.display = 'none';
                amountSplitSection.style.display = 'block';
                validateAmounts();
            }
        }
        
        function updateEqualSplit() {
            const amount = parseFloat(document.getElementById('amount').value) || 0;
            const checkboxes = document.querySelectorAll('.member-checkbox:checked');
            const memberCount = checkboxes.length;
            
            if (memberCount === 0 || amount === 0) {
                document.querySelectorAll('.equal-amount').forEach(span => {
                    span.textContent = '₹0.00';
                });
                return;
            }
            
            const equalAmount = amount / memberCount;
            
            document.querySelectorAll('.member-checkbox').forEach(checkbox => {
                const amountSpan = checkbox.parentElement.querySelector('.equal-amount');
                if (checkbox.checked) {
                    amountSpan.textContent = '₹' + equalAmount.toFixed(2);
                } else {
                    amountSpan.textContent = '₹0.00';
                }
            });
        }
        
        function validatePercentages() {
            const inputs = document.querySelectorAll('.percentage-input');
            let total = 0;
            
            inputs.forEach(input => {
                total += parseFloat(input.value) || 0;
            });
            
            document.getElementById('totalPercentage').textContent = 'Total: ' + total.toFixed(2) + '%';
            
            if (Math.abs(total - 100) > 0.01) {
                document.getElementById('totalPercentage').style.color = 'red';
            } else {
                document.getElementById('totalPercentage').style.color = 'green';
            }
        }
        
        function validateAmounts() {
            const totalExpenseAmount = parseFloat(document.getElementById('amount').value) || 0;
            const inputs = document.querySelectorAll('.amount-input');
            let total = 0;
            
            inputs.forEach(input => {
                total += parseFloat(input.value) || 0;
            });
            
            document.getElementById('totalAmount').textContent = 'Total: ₹' + total.toFixed(2);
            
            if (Math.abs(total - totalExpenseAmount) > 0.01) {
                document.getElementById('totalAmount').style.color = 'red';
            } else {
                document.getElementById('totalAmount').style.color = 'green';
            }
        }
        
        // Initialize the page
        window.onload = function() {
            updateEqualSplit();
        };
    </script>
</body>
</html> 