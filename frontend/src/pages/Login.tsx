import { useState, useEffect } from "react";
import { login } from "../api/userApi";
import { useUser } from "../viewmodels/UserViewModel";
import { useNavigate } from "react-router-dom";
import logo from "../assets/LogoParaInicio.png";

const Login = () => {
  const [username, setUsername] = useState<string>("");
  const [password, setPassword] = useState<string>("");
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
      <img src={logo} alt="Logo" className="login-logo" />
      <h2>Ingresa a SmartLogix</h2>

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