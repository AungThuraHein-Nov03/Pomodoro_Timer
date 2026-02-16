package com.aungthurahein.myapplicationpmt

object MotivationalQuotes {

    data class Quote(val text: String, val author: String)

    private val quotes = listOf(
        Quote("The secret of getting ahead is getting started.", "Mark Twain"),
        Quote("Focus on being productive instead of busy.", "Tim Ferriss"),
        Quote("It's not that I'm so smart, it's just that I stay with problems longer.", "Albert Einstein"),
        Quote("Do the hard jobs first. The easy jobs will take care of themselves.", "Dale Carnegie"),
        Quote("Action is the foundational key to all success.", "Pablo Picasso"),
        Quote("Concentrate all your thoughts upon the work at hand.", "Alexander Graham Bell"),
        Quote("Either you run the day, or the day runs you.", "Jim Rohn"),
        Quote("Start where you are. Use what you have. Do what you can.", "Arthur Ashe"),
        Quote("Productivity is never an accident. It is always the result of a commitment to excellence.", "Paul J. Meyer"),
        Quote("You don't have to be great to start, but you have to start to be great.", "Zig Ziglar"),
        Quote("The way to get started is to quit talking and begin doing.", "Walt Disney"),
        Quote("Small daily improvements are the key to staggering long-term results.", "Robin Sharma"),
        Quote("Amateurs sit and wait for inspiration. The rest of us just get up and go to work.", "Stephen King"),
        Quote("Your future is created by what you do today, not tomorrow.", "Robert Kiyosaki"),
        Quote("Don't count the days; make the days count.", "Muhammad Ali"),
        Quote("Success is the sum of small efforts repeated day in and day out.", "Robert Collier"),
        Quote("The only way to do great work is to love what you do.", "Steve Jobs"),
        Quote("What we fear doing most is usually what we most need to do.", "Tim Ferriss"),
        Quote("It always seems impossible until it's done.", "Nelson Mandela"),
        Quote("Discipline is the bridge between goals and accomplishment.", "Jim Rohn"),
        Quote("You miss 100% of the shots you don't take.", "Wayne Gretzky"),
        Quote("The best time to plant a tree was 20 years ago. The second best time is now.", "Chinese Proverb"),
        Quote("Be not afraid of going slowly; be afraid only of standing still.", "Chinese Proverb"),
        Quote("A year from now you will wish you had started today.", "Karen Lamb"),
        Quote("Work hard in silence, let your success be your noise.", "Frank Ocean"),
    )

    private var lastIndex = -1

    fun getRandom(): Quote {
        if (quotes.size <= 1) return quotes.first()
        var index: Int
        do {
            index = quotes.indices.random()
        } while (index == lastIndex)
        lastIndex = index
        return quotes[index]
    }
}
