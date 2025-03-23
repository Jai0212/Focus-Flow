# Focus Flow

<img width="200" alt="Focus Flow Logo" src="https://github.com/user-attachments/assets/95af2975-0bf1-4c5f-a74c-134f8740bb92" />

Focus Flow is an app designed to promote healthier digital habits by reducing impulsive app usage 
and screen time. By leveraging principles of Human-Computer Interaction (HCI), intuitive UI/UX 
design, and behavior modification techniques, Focus Flow encourages users to adopt better digital 
habits, ultimately improving their overall well-being.

The app allows users to select which apps they find distracting. Whenever they attempt to access 
these apps, Focus Flow displays a thoughtful prompt, encouraging them to reconsider their actions 
and make healthier choices.

This project is part of a digital well-being research initiative focused on identifying the most 
effective prompts to help users reduce screen time and rethink impulsive app usage.

<img width="200" alt="Block Page" src="https://github.com/user-attachments/assets/8b2806dc-f184-4bf3-affa-c7bb9b0e6f6c" />
<img width="200" alt="List Page" src="https://github.com/user-attachments/assets/bd60c947-df47-482d-af7b-cf8df95fb4de" />

## Features

- **Custom Prompts**: Display personalized prompts to encourage users to pause and reconsider opening distracting apps.
- **Breathing Exercises**: Interactive, guided breathing exercises help users manage impulsive app usage by encouraging mindful moments.
- **Behavioral Nudges**: The app uses gentle reminders and prompts to encourage users to take breaks from their screens and engage in healthier activities.
- **Usage Tracking**: Real-time analytics on screen time and app usage help users identify patterns and make informed decisions about their digital behavior.
- **Customizable Goals**: Users can set personalized goals for reducing screen time and improving overall digital well-being.


- **Statistical Methods for Prompt Selection**: We use statistical methods to determine which prompt to display next based on their effectiveness. The methods include:
    1. **Weighted Random Selection (Stochastic Selection)**: Prompts are selected probabilistically based on their weight, which is calculated using success rates and adjusted for cooldowns. This ensures higher-performing prompts are more likely to be shown.
    2. **Cooldown Period (Decay Function)**: A cooldown mechanism reduces the likelihood of showing the same prompt consecutively by applying an exponential decay function. This helps to avoid over-relying on top-performing prompts.
    3. **Success Rate Calculation (Descriptive Statistics)**: Success rates for each prompt are calculated using proportions (successes / total trials) to assess their effectiveness in encouraging healthier app usage.
    4. **Sorting and Ranking**: Prompts are ranked based on their success rates, with higher-ranked prompts receiving higher weights and being prioritized for selection.


## Tech Stack

- **Kotlin**: The app is developed using Kotlin, ensuring smooth performance and modern Android development practices.
- **Firebase**: Firebase is used for authentication, real-time database storage, and analytics to track and manage user data securely.
- **Human-Computer Interaction (HCI) Principles**: The app applies HCI principles to design a seamless and user-friendly experience.
- **UI/UX Design**: Intuitive, minimalistic design elements enhance usability and promote healthy app interaction.


<img width="200" alt="Block Page" src="https://github.com/user-attachments/assets/de33b3a2-6ac3-439a-94a0-c2e46ae16856" />
<img width="200" alt="Block Page" src="https://github.com/user-attachments/assets/5170d0bd-58d3-4aaf-b946-24688e6a4598" />

## Acknowledgement
This project was developed by:
- [Jai Joshi](https://github.com/Jai0212)
- [Joe Fang](https://github.com/MinecraftFuns)
