import { useState } from "react";
import { register } from "../api/userApi";
import { useNavigate } from "react-router-dom";

export default function Register() {

  const navigate = useNavigate();

  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleRegister = async () => {

    try {

      await register({
        username,
        email,
        password
      });

      alert("Usuario registrado correctamente");

      navigate("/");

    } catch {

      alert("Error al registrar usuario");
    }
  };

  return (
    <div className="page">

      <h2>Registro SmartLogix</h2>

      <input
        type="text"
        placeholder="Usuario"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
      />

      <input
        type="email"
        placeholder="Correo"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
      />

      <input
        type="password"
        placeholder="Contraseña"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
      />

      <button onClick={handleRegister}>
        Registrarse
      </button>

    </div>
  );
}