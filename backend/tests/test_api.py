import unittest
from fastapi.testclient import TestClient
from app.main import app


class TestEchoStateAPI(unittest.TestCase):
    def setUp(self):
        self.client = TestClient(app)

    def test_root(self):
        """Verify root endpoint responds with service metadata."""
        response = self.client.get("/")
        self.assertEqual(response.status_code, 200)
        data = response.json()
        self.assertIn("EchoState", data["message"])
        self.assertIn("/docs", data["documentation"])
        self.assertIn("/health", data["health"])

    def test_health_check(self):
        """Verify Railway and system healthcheck endpoint."""
        response = self.client.get("/health")
        self.assertEqual(response.status_code, 200)
        data = response.json()
        self.assertEqual(data["status"], "healthy")
        self.assertIn("models", data)
        self.assertEqual(data["models"]["vision"], "gemini-3.8-flash")
        self.assertEqual(data["models"]["live_audio"], "gemini-3.8-live")


if __name__ == "__main__":
    unittest.main()
