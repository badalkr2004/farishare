<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>500 - Server Error</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f8f9fa;
            padding-top: 50px;
        }
        .error-container {
            text-align: center;
            max-width: 600px;
            margin: 0 auto;
            padding: 40px 20px;
        }
        .error-code {
            font-size: 120px;
            color: #e74c3c;
            font-weight: bold;
            margin-bottom: 0;
        }
        .error-message {
            font-size: 24px;
            margin-bottom: 30px;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="error-container">
            <h1 class="error-code">500</h1>
            <p class="error-message">Internal Server Error</p>
            <p>Something went wrong on our end. Please try again later.</p>
            <a href="${pageContext.request.contextPath}/" class="btn btn-primary">Go Home</a>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 