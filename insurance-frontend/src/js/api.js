const API_BASE_URL = 'http://localhost:4004';

let authToken = localStorage.getItem('token');


async function apiCall(endpoint, method = 'GET', body = null) {
    const headers = {
        'Content-Type': 'application/json'
    };

    if (authToken) {
        headers['Authorization'] = `Bearer ${authToken}`;
    }

    const config = {
        method: method,
        headers: headers
    };

    if (body) {
        config.body = JSON.stringify(body);
    }

    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, config);

        if (response.status === 401) {
            localStorage.removeItem('token');
            localStorage.removeItem('userEmail');
            window.location.href = '/login.html';
            return null;
        }

        return response;
    } catch (error) {
        console.error('API Error:', error);
        return null;
    }
}


async function login(email, password) {
    const response = await apiCall('/login', 'POST', { email, password });
    if (response && response.ok) {
        const data = await response.json();
        authToken = data.token;
        localStorage.setItem('token', authToken);
        localStorage.setItem('userEmail', email);
        return true;
    }
    return false;
}

async function register(email, password, role) {
    const response = await apiCall('/register', 'POST', {
        email,
        password,
        confirmPassword: password,
        role
    });
    return response && response.ok;
}

async function forgotPassword(email) {
    const response = await apiCall('/forgot-password', 'POST', { email });
    return response && response.ok;
}

async function resetPassword(token, newPassword) {
    const response = await apiCall('/reset-password', 'POST', {
        token,
        newPassword,
        confirmPassword: newPassword
    });
    return response && response.ok;
}

function logout() {
    authToken = null;
    localStorage.removeItem('token');
    localStorage.removeItem('userEmail');
    window.location.href = '/login.html';
}


async function getClients() {
    const response = await apiCall('/api/clients');
    if (response && response.ok) {
        return await response.json();
    }
    return [];
}

async function getClientById(id) {
    const response = await apiCall(`/api/clients/${id}`);
    if (response && response.ok) {
        return await response.json();
    }
    return null;
}

async function createClient(clientData) {
    const response = await apiCall('/api/clients', 'POST', clientData);
    return response && response.ok;
}

async function updateClient(id, clientData) {
    const response = await apiCall(`/api/clients/${id}`, 'PUT', clientData);
    return response && response.ok;
}

async function deleteClient(id) {
    const response = await apiCall(`/api/clients/${id}`, 'DELETE');
    return response && response.ok;
}


async function getCars() {
    const response = await apiCall('/api/cars');
    if (response && response.ok) {
        return await response.json();
    }
    return [];
}

async function getCarById(id) {
    const response = await apiCall(`/api/cars/${id}`);
    if (response && response.ok) {
        return await response.json();
    }
    return null;
}

async function createCar(carData) {
    const response = await apiCall('/api/cars', 'POST', carData);
    return response && response.ok;
}

async function updateCar(id, carData) {
    const response = await apiCall(`/api/cars/${id}`, 'PUT', carData);
    return response && response.ok;
}

async function deleteCar(id) {
    const response = await apiCall(`/api/cars/${id}`, 'DELETE');
    return response && response.ok;
}


async function getDrivers() {
    const response = await apiCall('/api/drivers');
    if (response && response.ok) {
        return await response.json();
    }
    return [];
}

async function createDriver(driverData) {
    const response = await apiCall('/api/drivers', 'POST', driverData);
    return response && response.ok;
}

async function updateDriver(id, driverData) {
    const response = await apiCall(`/api/drivers/${id}`, 'PUT', driverData);
    return response && response.ok;
}

async function deleteDriver(id) {
    const response = await apiCall(`/api/drivers/${id}`, 'DELETE');
    return response && response.ok;
}


async function getPolicies() {
    const response = await apiCall('/api/policies');
    if (response && response.ok) {
        return await response.json();
    }
    return [];
}

async function getPolicyByNumber(policyNumber) {
    const response = await apiCall(`/api/policies/${policyNumber}`);
    if (response && response.ok) {
        return await response.json();
    }
    return null;
}

async function createPolicy(policyData) {
    const response = await apiCall('/api/policies', 'POST', policyData);
    return response && response.ok;
}

async function updatePolicy(policyNumber, policyData) {
    const response = await apiCall(`/api/policies/${policyNumber}`, 'PUT', policyData);
    return response && response.ok;
}

async function deletePolicy(id) {
    const response = await apiCall(`/api/policies/${id}`, 'DELETE');
    return response && response.ok;
}

async function downloadPolicyPDF(policyNumber) {
    const response = await apiCall(`/api/policies/${policyNumber}/download`);
    if (response && response.ok) {
        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `insurance_policy_${policyNumber}.pdf`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
        return true;
    }
    return false;
}


let chatSessionId = localStorage.getItem('chatSessionId');
if (!chatSessionId) {
    chatSessionId = 'session-' + Date.now();
    localStorage.setItem('chatSessionId', chatSessionId);
}

async function sendChatMessage(message) {
    const response = await apiCall('/api/chat/message', 'POST', {
        sessionId: chatSessionId,
        message: message
    });
    if (response && response.ok) {
        return await response.json();
    }
    return null;
}