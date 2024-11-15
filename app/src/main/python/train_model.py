import torch
import torch.nn as nn
import pandas as pd
from torch.optim import Adam
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler

# Sample data
data = [
    {"id": 1, "name": "Prompt 1", "successes": 50, "failures": 30, "showCount": 5, "lastShown": 0},
    {"id": 2, "name": "Prompt 2", "successes": 40, "failures": 60, "showCount": 3, "lastShown": 0},
    {"id": 3, "name": "Prompt 3", "successes": 75, "failures": 25, "showCount": 2, "lastShown": 0},
    {"id": 4, "name": "Prompt 4", "successes": 30, "failures": 70, "showCount": 7, "lastShown": 0}
]

# Convert to DataFrame
df = pd.DataFrame(data)

# Feature Engineering
df['success_rate'] = df['successes'] / (df['successes'] + df['failures'])
df['failure_rate'] = df['failures'] / (df['successes'] + df['failures'])

# Define the features and target
X = df[['success_rate', 'failure_rate', 'showCount']].values
y = df['success_rate'].values

# Scaling features
scaler = StandardScaler()
X_scaled = scaler.fit_transform(X)

# Convert to PyTorch tensors
X_tensor = torch.tensor(X_scaled, dtype=torch.float32)
y_tensor = torch.tensor(y, dtype=torch.float32)

# Train-Test Split
X_train, X_test, y_train, y_test = train_test_split(X_tensor, y_tensor, test_size=0.2, random_state=42)

# Define the Model
class PromptRankingModel(nn.Module):
    def __init__(self):
        super(PromptRankingModel, self).__init__()
        self.fc1 = nn.Linear(3, 64)  # Input layer (3 features)
        self.fc2 = nn.Linear(64, 32)
        self.fc3 = nn.Linear(32, 1)  # Output layer (1 target: success_rate)

    def forward(self, x):
        x = torch.relu(self.fc1(x))  # Activation function
        x = torch.relu(self.fc2(x))
        x = self.fc3(x)  # Output is a single scalar (predicted success rate)
        return x

# Instantiate the model
model = PromptRankingModel()

# Loss function and optimizer
criterion = nn.MSELoss()  # Mean Squared Error loss for regression
optimizer = Adam(model.parameters(), lr=0.001)

# Training Loop
num_epochs = 1000
for epoch in range(num_epochs):
    model.train()
    optimizer.zero_grad()

    # Forward pass
    y_pred = model(X_train)

    # Calculate the loss
    loss = criterion(y_pred.squeeze(), y_train)

    # Backward pass and optimization
    loss.backward()
    optimizer.step()

    if (epoch + 1) % 100 == 0:
        print(f'Epoch [{epoch+1}/{num_epochs}], Loss: {loss.item():.4f}')

# Evaluate the model
model.eval()
with torch.no_grad():
    y_pred = model(X_test)
    test_loss = criterion(y_pred.squeeze(), y_test)
    print(f'Test Loss: {test_loss.item():.4f}')

# Rank prompts based on predicted success rate
df['predicted_success_rate'] = model(X_tensor).detach().numpy()
df_sorted = df.sort_values(by='predicted_success_rate', ascending=False)

# Display the next prompt to show
next_prompt = df_sorted.iloc[0]
print(f"The next prompt to show is: {next_prompt['name']}")
