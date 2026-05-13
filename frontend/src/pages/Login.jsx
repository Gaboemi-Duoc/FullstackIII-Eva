import { useState, useEffect } from "react";
import { login } from "../api/userApi";
import { useUser } from "../viewmodels/UserViewModel";
import { useNavigate } from "react-router-dom";

const Login = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const { setUser } = useUser();
  const navigate = useNavigate();

  // Si ya hay token, redirigir al inventario automáticamente
  useEffect(() => {
    const token = localStorage.getItem("authToken");
    if (token) {
      navigate("/inventory");
    }
  }, [navigate]);

  const handleLogin = async () => {
    try {
      const userData = await login(username, password);
      setUser(userData);
      navigate("/inventory");
    } catch (error) {
      alert("Error al iniciar sesión");
    }
  };

  return (
    <div className="page">
      <h2>Login</h2>

      <input
        type="text"
        placeholder="Usuario"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
      />

      <input
        type="password"
        placeholder="Contraseña"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
      />

      <button onClick={handleLogin}>Ingresar</button>
    </div>
  );
};

export default Login;