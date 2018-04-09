/*
 * Copyright (C) 2017 Chan Chung Kwong <1m02math@126.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cc.fooledit.editor.image;
import cc.fooledit.*;
import static cc.fooledit.core.DataObjectTypeRegistry.addDataEditor;
import static cc.fooledit.core.DataObjectTypeRegistry.addDataObjectType;
import cc.fooledit.core.*;
import cc.fooledit.spi.*;
import java.util.function.*;
import java.util.logging.*;
import javafx.scene.effect.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ImageEditorModule{
	public static final String NAME="editor.image";
	public static final String EFFECT="effect";
	public static final RegistryNode<String,?,String> REGISTRY=Registry.ROOT.getChild(NAME);
	public static final RegistryNode<String,EffectTool,String> EFFECT_REGISTRY=(RegistryNode<String,EffectTool,String>)REGISTRY.getOrCreateChild(EFFECT);
	public static void onLoad(){
		addDataObjectType(ImageObjectType.INSTANCE);
		addDataEditor(new IconEditor(),ImageObject.class);
		addDataEditor(new ImageEditor(),ImageObject.class);
		addDataEditor(new GraphicsEditor(),ImageObject.class);
		CoreModule.TEMPLATE_TYPE_REGISTRY.addChild(ImageTemplate.class.getName(),(obj)->new ImageTemplate((String)obj.get("name"),(String)obj.get("description"),(String)obj.get("file"),(String)obj.get("mime")));
		addEffect("BLOOM",()->new Bloom());
		addEffect("BOX_BLUR",()->new BoxBlur());
		addEffect("COLOR_ADJUST",()->new ColorAdjust());
		addEffect("COLOR_INPUT",()->new ColorInput());
		addEffect("DISPLACEMENT_MAP",()->new DisplacementMap());
		addEffect("DROP_SHADOW",()->new DropShadow());
		addEffect("GAUSSIAN_BLUR",()->new GaussianBlur());
		addEffect("GLOW",()->new Glow());
		addEffect("IMAGE_INPUT",()->new ImageInput());
		addEffect("INNER_SHADOW",()->new InnerShadow());
		addEffect("LIGHTING",()->new Lighting());
		addEffect("MOTION_BLUR",()->new MotionBlur());
		addEffect("PERSPECTIVE_TRANSFORM",()->new PerspectiveTransform());
		addEffect("SEPIA_TONE",()->new SepiaTone());
		addEffect("SHADOW",()->new Shadow());
	}
	private static void addEffect(String name,Supplier<Effect> effect){
		EFFECT_REGISTRY.addChild(name,new SimpleEffectTool(name,()->null,(n)->effect.get()));
	}
	public static void onUnLoad(){

	}
	public static void onInstall(){
		Registry.providesDataObjectType(ImageObjectType.class.getName(),NAME);
		Registry.providesDataObjectEditor(ImageEditor.class.getName(),NAME);
		Registry.providesDataObjectEditor(IconEditor.class.getName(),NAME);
		Registry.providesDataObjectEditor(GraphicsEditor.class.getName(),NAME);
		Registry.providesTypeToEditor(ImageObject.class.getName(),NAME);
		Registry.providesTemplateType(ImageTemplate.class.getName(),NAME);
		try{
			((ListRegistryNode)CoreModule.TEMPLATE_REGISTRY.getOrCreateChild("children")).addChild(
					StandardSerializiers.JSON_SERIALIZIER.decode(Helper.readText(Main.INSTANCE.getFile("templates.json",NAME))));
		}catch(Exception ex){
			Logger.getGlobal().log(Level.INFO,null,ex);
		}
	}
}
