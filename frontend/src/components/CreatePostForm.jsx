import { useState } from 'react';
import axios from 'axios';
import { toast } from 'react-toastify';

function CreatePostForm() {
  const [formData, setFormData] = useState({
    title: '',
    content: '',
    category: '',
    tags: '',
    featuredImages: [], // Array to store up to 3 images
    video: null,
    publishImmediately: false,
  });

  const handleChange = (e) => {
    const { name, value, type, checked, files } = e.target;
    if (type === 'file' && name === 'featuredImages') {
      const selectedFiles = Array.from(files);
      if (selectedFiles.length + formData.featuredImages.length > 3) {
        toast.error('You can only upload up to 3 images!', {
          position: 'top-right',
          autoClose: 3000,
        });
        e.target.value = ''; // Reset the input
        return;
      }
      setFormData((prev) => ({
        ...prev,
        featuredImages: [...prev.featuredImages, ...selectedFiles], // Append new files to existing ones
      }));
      e.target.value = ''; // Clear the input to allow re-selection
    } else if (type === 'file' && name === 'video') {
      setFormData((prev) => ({
        ...prev,
        video: files[0],
      }));
    } else {
      setFormData((prev) => ({
        ...prev,
        [name]: type === 'checkbox' ? checked : value,
      }));
    }
  };

  // Function to remove a selected image
  const removeImage = (index) => {
    setFormData((prev) => ({
      ...prev,
      featuredImages: prev.featuredImages.filter((_, i) => i !== index),
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Validate required fields
    if (!formData.title) {
      toast.error('Title is required!', { position: 'top-right', autoClose: 3000 });
      return;
    }
    if (!formData.content) {
      toast.error('Content is required!', { position: 'top-right', autoClose: 3000 });
      return;
    }
    if (!formData.category) {
      toast.error('Category is required!', { position: 'top-right', autoClose: 3000 });
      return;
    }
    if (!formData.tags) {
      toast.error('Tags are required!', { position: 'top-right', autoClose: 3000 });
      return;
    }
    if (formData.featuredImages.length === 0) {
      toast.error('At least one image is required!', { position: 'top-right', autoClose: 3000 });
      return;
    }

    const data = new FormData();
    data.append('title', formData.title);
    data.append('text', formData.content);
    data.append('creatorId', 'user123');
    data.append('creatorName', 'Test User');
    data.append('category', formData.category);
    data.append('tags', formData.tags);
    data.append('published', formData.publishImmediately.toString());

    formData.featuredImages.forEach((image) => {
      data.append('images', image);
    });

    if (formData.video) {
      data.append('video', formData.video);
    }

    try {
      const response = await axios.post('http://localhost:8080/api/posts', data, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });

      toast.success('Post created successfully!', { position: 'top-right', autoClose: 3000 });
      console.log('Post created:', response.data);

      setFormData({
        title: '',
        content: '',
        category: '',
        tags: '',
        featuredImages: [],
        video: null,
        publishImmediately: false,
      });

      document.querySelector('input[name="featuredImages"]').value = '';
      document.querySelector('input[name="video"]').value = '';
    } catch (error) {
      const errorMessage = error.response?.data || error.message;
      toast.error(`Failed to create post: ${errorMessage}`, { position: 'top-right', autoClose: 5000 });
      console.error('Error creating post:', errorMessage);
    }
  };

  const handleCancel = () => {
    setFormData({
      title: '',
      content: '',
      category: '',
      tags: '',
      featuredImages: [],
      video: null,
      publishImmediately: false,
    });

    document.querySelector('input[name="featuredImages"]').value = '';
    document.querySelector('input[name="video"]').value = '';
  };

  const handlePreview = () => {
    console.log('Previewing post:', formData);
  };

  return (
    <div className="container mx-auto p-6">
      <h2 className="text-2xl font-bold mb-6">Create Post</h2>
      <form onSubmit={handleSubmit} className="bg-white p-6 rounded-lg shadow-md" encType="multipart/form-data">
        <div className="mb-4">
          <label className="block text-gray-700 font-medium mb-2">Title</label>
          <input
            type="text"
            name="title"
            value={formData.title}
            onChange={handleChange}
            placeholder="Enter post title"
            className="w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <div className="mb-4">
          <label className="block text-gray-700 font-medium mb-2">Content</label>
          <textarea
            name="content"
            value={formData.content}
            onChange={handleChange}
            placeholder="Write your post content here..."
            className="w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 h-32"
          />
        </div>

        <div className="flex space-x-4 mb-4">
          <div className="w-1/2">
            <label className="block text-gray-700 font-medium mb-2">Category</label>
            <select
              name="category"
              value={formData.category}
              onChange={handleChange}
              className="w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="" disabled>Select category</option>
              <option value="Strength">Strength</option>
              <option value="Cardio">Cardio</option>
              <option value="Yoga">Yoga</option>
              <option value="HIIT">HIIT</option>
            </select>
          </div>

          <div className="w-1/2">
            <label className="block text-gray-700 font-medium mb-2">Tags</label>
            <input
              type="text"
              name="tags"
              value={formData.tags}
              onChange={handleChange}
              placeholder="Enter tags separated by commas (e.g., fitness, workout)"
              className="w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
        </div>

        <div className="mb-4">
          <label className="block text-gray-700 font-medium mb-2">Featured Images (up to 3)</label>
          <input
            type="file"
            name="featuredImages"
            onChange={handleChange}
            accept="image/*"
            multiple
            className="w-full p-2 border border-gray-300 rounded-md"
          />
          {/* Display selected image names */}
          {formData.featuredImages.length > 0 && (
            <div className="mt-2">
              <p className="text-gray-600">Selected Images:</p>
              <ul className="list-disc pl-5">
                {formData.featuredImages.map((image, index) => (
                  <li key={index} className="flex items-center justify-between text-gray-700">
                    <span>{image.name}</span>
                    <button
                      type="button"
                      onClick={() => removeImage(index)}
                      className="text-red-500 hover:text-red-700 ml-2"
                    >
                      Remove
                    </button>
                  </li>
                ))}
              </ul>
            </div>
          )}
        </div>

        <div className="mb-4">
          <label className="block text-gray-700 font-medium mb-2">Video Upload (Optional)</label>
          <input
            type="file"
            name="video"
            onChange={handleChange}
            accept="video/*"
            className="w-full p-2 border border-gray-300 rounded-md"
          />
        </div>

        <div className="mb-4 flex items-center">
          <input
            type="checkbox"
            name="publishImmediately"
            checked={formData.publishImmediately}
            onChange={handleChange}
            className="mr-2"
          />
          <label className="text-gray-700 font-medium">Publish Immediately</label>
        </div>

        <div className="flex space-x-4">
          <button
            type="button"
            onClick={handlePreview}
            className="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-100"
          >
            Show Preview
          </button>
          <button
            type="button"
            onClick={handleCancel}
            className="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-100"
          >
            Cancel
          </button>
          <button
            type="submit"
            className="px-4 py-2 bg-black text-white rounded-md hover:bg-gray-800"
          >
            Create Post
          </button>
        </div>
      </form>
    </div>
  );
}

export default CreatePostForm;