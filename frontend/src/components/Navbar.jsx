import { Link, useNavigate } from "react-router-dom";
import { useUser } from "../viewmodels/UserViewModel";
import logo from "../assets/LogoConBorde.png";

const Navbar = () => {
  const { user, setUser } = useUser();
  const navigate = useNavigate();

  const token = localStorage.getItem("authToken");

  const handleLogout = () => {
    localStorage.removeItem("authToken");
    localStorage.removeItem("userId");
    setUser(null);
    navigate("/");
  };

  return (
    <div className="navbar">
      <div className="nav-left">
        <img src={logo} alt="SmartLogix Logo" className="navbar-logo" />
        <h3>SmartLogix</h3>
      </div>

      {token && (
        <div className="nav-links">
          <Link to="/inventory">Inventario</Link>
          <Link to="/profile">Perfil</Link>

          <button className="logout-btn" onClick={handleLogout}>
            Logout
          </button>

          <div className="avatar">
            {user?.username ? user.username.charAt(0).toUpperCase() : "U"}
          </div>
        </div>
      )}
    </div>
  );
};

export default Navbar;