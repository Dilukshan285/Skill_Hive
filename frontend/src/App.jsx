import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import Navbar from './components/Navbar';
import ProtectedRoute from './components/ProtectedRoute';
import './style.css';
import QuizzesPage from './pages/QuizzesPage';
import QuizDetailsPage from './pages/QuizDetailsPage';
import QuizEditPage from './pages/QuizEditPage';
import QuestionEditPage from './pages/QuestionEditPage';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css'; // Import the CSS for react-toastify
import Navbar from './components/Navbar_2';
import HomePage from './pages/HomePage';
import CreatePostPage from './pages/CreatePostPage';


function App() {
  return (
    <BrowserRouter>
      <Navbar />

      <div className="min-h-screen bg-gray-100">
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/create-post" element={<CreatePostPage />} />
        </Routes>
        <ToastContainer
          position="top-right"
          autoClose={3000}
          hideProgressBar={false}
          newestOnTop={false}
          closeOnClick
          rtl={false}
          pauseOnFocusLoss
          draggable
          pauseOnHover
        />
      </div>

      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        
        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <Dashboard />
            </ProtectedRoute>
          }
        />
        <Route path="/quizzes" element={<QuizzesPage />} />
        <Route path="/quizzes/:quizId" element={<QuizDetailsPage />} />
        <Route path="/quizzes/:quizId/edit" element={<QuizEditPage />} />
        <Route path="/quizzes/:quizId/questions/:questionId/edit" element={<QuestionEditPage />} />
        
        <Route path="/" element={<Login />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;