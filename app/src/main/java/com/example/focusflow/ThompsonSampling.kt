package com.example.focusflow

import kotlin.math.pow
import kotlin.random.Random

class ThompsonSampling {
    fun selectPrompt(prompts: Iterable<Prompt>): Prompt {
        val betaDistributions = prompts.map { prompt ->
            prompt to randomBeta(prompt.successes + 1, prompt.failures + 1)
        }

        return betaDistributions.maxByOrNull { it.second }?.first ?: prompts.first()
    }

    fun updatePrompt(prompt: Prompt, reward: Boolean) {
        if (reward) {
            prompt.successes += 1
        } else {
            prompt.failures += 1
        }
    }

    private fun randomBeta(alpha: Int, beta: Int): Double {
        val x = Random.nextDouble()
        val y = Random.nextDouble()

        val xTrans = x.pow(1.0 / alpha)
        val yTrans = y.pow(1.0 / beta)

        return xTrans / (xTrans + yTrans)
    }
}

fun main() {
    val thompsonSampling = ThompsonSampling()
    val prompts = listOf(
        Prompt(id = 1, name = "What are you hoping to achieve by opening this app right now?", successes = 5, failures = 5, lastShown = 0,showCount=10),
        Prompt(id = 2, name = "How do you feel in this moment? Will this app help or hinder that feeling?", successes = 3, failures = 7, lastShown = 0,showCount=10),
        Prompt(id = 3, name = "Is there something more meaningful you could focus on right now?", successes = 8, failures = 2, lastShown = 0,showCount=10),
        Prompt(id = 4, name = "Are you reaching for this app out of habit or purpose?", successes = 6, failures = 4, lastShown = 0,showCount=10),
        Prompt(id = 5, name = "What’s one small step you could take toward a personal goal instead?", successes = 4, failures = 6, lastShown = 0,showCount=10),
    )

    for (i in 1..10) { // Run multiple iterations to test selection and update
        val selectedPrompt = thompsonSampling.selectPrompt(prompts)
        println("Iteration $i: Selected Prompt: '${selectedPrompt.name}' with Successes: ${selectedPrompt.successes}, Failures: ${selectedPrompt.failures}")

        // Simulate reward, assuming a random outcome for this example:
        val reward = Random.nextBoolean()
        println("  Reward: $reward")
        thompsonSampling.updatePrompt(selectedPrompt, reward)

        println("  Updated Prompt: '${selectedPrompt.name}' with Successes: ${selectedPrompt.successes}, Failures: ${selectedPrompt.failures}")
    }
}
