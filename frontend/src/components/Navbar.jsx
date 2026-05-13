import { Link, useNavigate } from "react-router-dom";
import { useUser } from "../viewmodels/UserViewModel";

const Navbar = () => {
  const { user, setUser } = useUser();
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem("authToken");
    localStorage.removeItem("userId");
    setUser(null);
    navigate("/");
  };

  return (
    <div className="navbar">
      <h3>SmartLogix</h3>

      {user && (
        <div className="nav-links">
          <Link to="/inventory">Inventario</Link>
          <Link to="/profile">Perfil</Link>

          <button className="logout-btn" onClick={handleLogout}>
            Logout
          </button>

          <div className="avatar">
            {user.username.charAt(0).toUpperCase()}
          </div>
        </div>
      )}
    </div>
  );
};

export default Navbar;