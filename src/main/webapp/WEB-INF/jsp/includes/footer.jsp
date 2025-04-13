<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<footer class="footer mt-auto py-3 bg-light">
    <div class="container">
        <div class="row">
            <div class="col-md-6">
                <p class="mb-0 text-muted">&copy; ${java.time.Year.now().getValue()} FairShareBU - Expense Sharing Made Easy</p>
            </div>
            <div class="col-md-6 text-md-end">
                <a href="${pageContext.request.contextPath}/about.jsp" class="text-muted me-2">About</a>
                <a href="${pageContext.request.contextPath}/privacy.jsp" class="text-muted me-2">Privacy</a>
                <a href="${pageContext.request.contextPath}/terms.jsp" class="text-muted">Terms</a>
            </div>
        </div>
    </div>
</footer> 