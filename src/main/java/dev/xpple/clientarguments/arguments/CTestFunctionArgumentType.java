package dev.xpple.clientarguments.arguments;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.test.TestFunction;
import net.minecraft.test.TestFunctions;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class CTestFunctionArgumentType implements ArgumentType<TestFunction> {
	private static final Collection<String> EXAMPLES = Arrays.asList("techtests.piston", "techtests");

	public static CTestFunctionArgumentType testFunction() {
		return new CTestFunctionArgumentType();
	}

	public static TestFunction getFunction(final CommandContext<FabricClientCommandSource> context, final String name) {
		return context.getArgument(name, TestFunction.class);
	}

	@Override
	public TestFunction parse(StringReader stringReader) throws CommandSyntaxException {
		String string = stringReader.readUnquotedString();
		Optional<TestFunction> optional = TestFunctions.getTestFunction(string);
        if (optional.isEmpty()) {
            Message message = Text.literal("No such test: " + string);
            throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
        }
		return optional.get();
    }

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
		return suggestTestNames(context, builder);
	}

	public static <S> CompletableFuture<Suggestions> suggestTestNames(final CommandContext<S> context, final SuggestionsBuilder builder) {
		Stream<String> stream = TestFunctions.getTestFunctions().stream().map(TestFunction::templatePath);
		return CommandSource.suggestMatching(stream, builder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
