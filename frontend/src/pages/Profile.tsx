import { useState } from "react";
import { useUser } from "../viewmodels/UserViewModel";
import { updateUsername } from "../api/userApi";

const Profile = () => {
  const { user, setUser } = useUser();
  const [newUsername, setNewUsername] = useState<string>("");

  const handleUpdate = async () => {
    if (!user) return;
    
    try {
      const updatedUser = await updateUsername(user.id_user, newUsername);
      // Update the user state with the full user object
      setUser({
        ...user,
        username: updatedUser.username,
      });
      alert("Username actualizado");
      setNewUsername(""); // Clear the input
    } catch (error) {
      alert("Error al actualizar");
    }
  };

  if (!user) return <p>No hay usuario</p>;

  return (
    <div className="page">
      <h2>Perfil</h2>

      <p>Username actual: {user.username}</p>

      <input
        type="text"
        placeholder="Nuevo username"
        value={newUsername}
        onChange={(e) => setNewUsername(e.target.value)}
      />

      <button onClick={handleUpdate}>Actualizar</button>
    </div>
  );
};

export default Profile;