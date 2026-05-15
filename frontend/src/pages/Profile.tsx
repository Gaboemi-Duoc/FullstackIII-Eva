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
      setUser(updatedUser);
      alert("Username actualizado");
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
        placeholder="Nuevo username"
        value={newUsername}
        onChange={(e) => setNewUsername(e.target.value)}
      />

      <button onClick={handleUpdate}>Actualizar</button>
    </div>
  );
};

export default Profile;