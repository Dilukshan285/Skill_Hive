import { Link } from 'react-router-dom';

function Navbar() {
  return (
    <nav className="bg-gray-800 p-4">
      <div className="container mx-auto flex justify-between items-center">
        <h1 className="text-white text-xl font-bold">Gym Post App</h1>
        <div className="space-x-4">
          <Link to="/" className="text-white hover:text-gray-300">Home</Link>
          <Link to="/create-post" className="text-white hover:text-gray-300">Create Post</Link>
        </div>
      </div>
    </nav>
  );
}

export default Navbar;