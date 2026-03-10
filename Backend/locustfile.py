import time
from locust import HttpUser, task, between

class QuickstartUser(HttpUser):
    wait_time = between(1, 5)

    @task
    def set_poll(self):
        for item_id in range(10):
            payload = {
                "description": "Test",
                "title": "Umfrage zu Test",
                "elements": [
                    {
                        "label": "Test",
                        "placeholder": "Test Input",
                        "type": "INPUT",
                        "required": False
                    },
                    {
                        "required": True,
                        "type": "EMAIL",
                        "placeholder": "Test",
                        "label": "Hallo"
                    },
                    {
                        "required": True,
                        "type": "CHECKBOX",
                        "placeholder": "Test",
                        "label": "Hallo"
                    },
                    {
                        "required": True,
                        "type": "DATE",
                        "placeholder": "Test",
                        "label": "Hallo"
                    },
                    {
                        "required": True,
                        "type": "NUMBER",
                        "placeholder": "Test",
                        "label": "Hallo"
                    },
                    {
                        "required": True,
                        "type": "SLIDER",
                        "placeholder": "Test",
                        "label": "Hallo"
                    },
                    {
                        "required": True,
                        "type": "TEXT",
                        "placeholder": "Test",
                        "label": "Hallo"
                    }
                ]
            }


            headers = {"token": self.token}

            self.client.post(
                "api/polls/create",
                json=payload,
                headers=headers
            )

    def on_start(self):
        resp = self.client.post("api/auth/login", json={"email":"foo", "password":"bar"})

        if resp and resp.json().get("token"):
            self.token = resp.json()["token"]
        else:
            self.token = None