package com.example.focusflow

import kotlin.random.Random

// Define data class for prompt
data class Prompt(
    val id: Int,
    val name: String,
    var successes: Int,
    var failures: Int,
    var showCount: Int,
    var lastShown: Int,
    var successRate: Double = 0.0
)

// Function to calculate success rate
fun calculateSuccessRate(prompt: Prompt): Double {
    val total = prompt.successes + prompt.failures
    return if (total > 0) prompt.successes.toDouble() / total else 0.0
}

// Function to decide the next prompt based on strategy
fun decideNextPrompt(prompts: MutableList<Prompt>, currentTime: Int, cooldownPeriod: Int): Prompt {
    // Calculate success rates for each prompt
    prompts.forEach { it.successRate = calculateSuccessRate(it) }

    // Sort prompts by success rate in descending order
    val sortedPrompts = prompts.sortedByDescending { it.successRate }

    // Weighted random selection: reduce the weight of high-performing prompts
    var totalWeight = 0.0
    val weights = mutableListOf<Double>()
    sortedPrompts.forEach { prompt ->
        // If the prompt was shown recently, give it less weight (implement cooldown)
        val weight = if (currentTime - prompt.lastShown > cooldownPeriod) 1.0 else 0.5
        weights.add(weight * prompt.successRate)
        totalWeight += weight * prompt.successRate
    }

    // Choose the next prompt based on weighted probability
    val rand = Random.nextDouble(0.0, totalWeight)
    var runningTotal = 0.0
    var nextPrompt = sortedPrompts[0] // Default to the first prompt
    for (i in sortedPrompts.indices) {
        runningTotal += weights[i]
        if (runningTotal >= rand) {
            nextPrompt = sortedPrompts[i]
            break
        }
    }

    // Update the last shown time for the selected prompt
    nextPrompt.lastShown = currentTime
    nextPrompt.showCount += 1

    return nextPrompt
}

// Example usage
fun main() {
    // List of prompts
    val prompts = mutableListOf(
        Prompt(id = 1, name = "Prompt 1", successes = 50, failures = 30, showCount = 5, lastShown = 0),
        Prompt(id = 2, name = "Prompt 2", successes = 40, failures = 60, showCount = 3, lastShown = 0),
        Prompt(id = 3, name = "Prompt 3", successes = 75, failures = 25, showCount = 2, lastShown = 0),
        Prompt(id = 4, name = "Prompt 4", successes = 30, failures = 70, showCount = 7, lastShown = 0)
    )

    val currentTime = 10  // Example of the current "time" (e.g., a counter of prompt shows)
    val cooldownPeriod = 3 // Cooldown period (number of shows)

    val nextPrompt = decideNextPrompt(prompts, currentTime, cooldownPeriod)
    println("Next prompt to show: ${nextPrompt.name} with success rate ${"%.2f".format(nextPrompt.successRate)}")
}
