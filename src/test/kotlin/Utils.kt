fun getRandomWord(size: Int = 10) = List(size) { ('a'..'z').random() }.joinToString("")
