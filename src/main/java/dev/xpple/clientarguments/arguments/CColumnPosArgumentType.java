package dev.xpple.clientarguments.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.CoordinateArgument;
import net.minecraft.server.command.CommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColumnPos;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class CColumnPosArgumentType implements ArgumentType<CPosArgument> {
	private static final Collection<String> EXAMPLES = Arrays.asList("0 0", "~ ~", "~1 ~-2", "^ ^", "^-1 ^0");
	public static final SimpleCommandExceptionType INCOMPLETE_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.pos2d.incomplete"));

	public static CColumnPosArgumentType columnPos() {
		return new CColumnPosArgumentType();
	}

	public static ColumnPos getColumnPos(final CommandContext<FabricClientCommandSource> context, final String name) {
		BlockPos blockPos = context.getArgument(name, CPosArgument.class).toAbsoluteBlockPos(context.getSource());
		return new ColumnPos(blockPos.getX(), blockPos.getZ());
	}

	@Override
	public CPosArgument parse(final StringReader stringReader) throws CommandSyntaxException {
		int cursor = stringReader.getCursor();
		if (!stringReader.canRead()) {
			throw INCOMPLETE_EXCEPTION.createWithContext(stringReader);
		}
		CoordinateArgument coordinateArgument = CoordinateArgument.parse(stringReader);
        if (!stringReader.canRead() || stringReader.peek() != ' ') {
            stringReader.setCursor(cursor);
            throw INCOMPLETE_EXCEPTION.createWithContext(stringReader);
        }
		stringReader.skip();
		CoordinateArgument coordinateArgument2 = CoordinateArgument.parse(stringReader);
		return new CDefaultPosArgument(coordinateArgument, new CoordinateArgument(true, 0.0), coordinateArgument2);
    }

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
		if (!(context.getSource() instanceof CommandSource)) {
			return Suggestions.empty();
		}
		String string = builder.getRemaining();
		Collection<CommandSource.RelativePosition> collection;
		if (!string.isEmpty() && string.charAt(0) == '^') {
			collection = Collections.singleton(CommandSource.RelativePosition.ZERO_LOCAL);
		} else {
			collection = ((CommandSource)context.getSource()).getBlockPositionSuggestions();
		}

		return CommandSource.suggestColumnPositions(string, collection, builder, CommandManager.getCommandValidator(this::parse));
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
