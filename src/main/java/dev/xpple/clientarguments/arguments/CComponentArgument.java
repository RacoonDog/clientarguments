package dev.xpple.clientarguments.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.commands.ParserUtils;

import java.util.Arrays;
import java.util.Collection;

public class CComponentArgument implements ArgumentType<Component> {
	private static final Collection<String> EXAMPLES = Arrays.asList("\"hello world\"", "\"\"", "\"{\"text\":\"hello world\"}", "[\"\"]");
	public static final DynamicCommandExceptionType INVALID_COMPONENT_EXCEPTION = new DynamicCommandExceptionType(text -> Component.translatableEscape("argument.component.invalid", text));
	private final HolderLookup.Provider holderLookupProvider;

	private CComponentArgument(HolderLookup.Provider holderLookupProvider) {
		this.holderLookupProvider = holderLookupProvider;
	}

	public static CComponentArgument textComponent(CommandBuildContext buildContext) {
		return new CComponentArgument(buildContext);
	}

	public static Component getComponent(final CommandContext<FabricClientCommandSource> context, final String name) {
		return context.getArgument(name, Component.class);
	}

	@Override
	public Component parse(final StringReader stringReader) throws CommandSyntaxException {
		try {
			return ParserUtils.parseJson(this.holderLookupProvider, stringReader, ComponentSerialization.CODEC);
		} catch (Exception var4) {
			String string = var4.getCause() != null ? var4.getCause().getMessage() : var4.getMessage();
			throw INVALID_COMPONENT_EXCEPTION.createWithContext(stringReader, string);
		}
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
