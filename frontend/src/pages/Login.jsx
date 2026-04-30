import { useState } from "react";
import { login } from "../api/userApi";
import { useUser } from "../viewmodels/UserView";
import { useNavigate } from "react-router-dom";

const Login = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const { setUser } = useUser();
  const navigate = useNavigate();

  const handleLogin = async () => {
    try {
      const userData = await login(username, password);
      setUser(userData);
      navigate("/profile");
    } catch (error) {
      alert("Error al iniciar sesión");
    }
  };

  return (
    <div className="page">
      <h2>Login</h2>

      <input
        placeholder="Username"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
      />

      <input
        type="password"
        placeholder="Password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
      />

      <button onClick={handleLogin}>Login</button>
    </div>
  );
};

export default Login;