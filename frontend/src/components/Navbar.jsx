import { useUser } from "../viewmodels/UserView";

const Navbar = () => {
  const { user } = useUser();

  return (
    <div className="navbar">
      <h3>SmartLogix</h3>

      {user && (
        <div className="avatar">
          {user.username.charAt(0).toUpperCase()}
        </div>
      )}
    </div>
  );
};

export default Navbar;