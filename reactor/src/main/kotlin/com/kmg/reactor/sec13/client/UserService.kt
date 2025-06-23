package com.kmg.reactor.sec13.client

import reactor.util.context.Context
import java.util.function.Function

object UserService {

    private val USER_CATEGORY = mapOf(
        "sam" to "standard",
        "mike" to "prime"
    )

    fun userCategoryContext(): Function<Context, Context> {
        return Function<Context, Context> { ctx ->
            ctx.getOrEmpty<String>("user")
                .filter(USER_CATEGORY::containsKey)
                .map(USER_CATEGORY::get)
                .map { category -> ctx.put("category", category!!) }
            .orElse(Context.empty())
        }
    }
}
