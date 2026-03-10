from locust import HttpUser, task, between

class MySpringBootUser(HttpUser):
    wait_time = between(1, 5)

    @task(1)
    def get_hello(self):
        self.client.get("/api/polls/PHGW0641")
