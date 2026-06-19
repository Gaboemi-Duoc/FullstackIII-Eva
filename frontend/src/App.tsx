import { BrowserRouter, Routes, Route } from "react-router-dom";
import Login from "./pages/Login";
import Profile from "./pages/Profile";
import Inventory from "./pages/Inventory";
import Restock from "./pages/Restock";
import Orders from "./pages/Orders";
import Navbar from "./components/Navbar";
import { UserProvider } from "./viewmodels/UserViewModel";
import ProtectedRoute from "./components/ProtectedRoute";


function App() {
  return (
    <UserProvider>
      <BrowserRouter>
        <Navbar />

        <Routes>
          <Route path="/" element={<Login />} />

          <Route
            path="/inventory"
            element={
              <ProtectedRoute>
                <Inventory />
              </ProtectedRoute>
            }
          />

          <Route
            path="/restock"
            element={
              <ProtectedRoute>
                <Restock />
              </ProtectedRoute>
            }
          />

          <Route
            path="/profile"
            element={
              <ProtectedRoute>
                <Profile />
              </ProtectedRoute>
            }
          />
          
          <Route
            path="/orders"
            element={
              <ProtectedRoute>
                <Orders />
              </ProtectedRoute>
            }
          />
        </Routes>
      </BrowserRouter>
    </UserProvider>
  );
}

export default App;