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
import cc.fooledit.editor.image.Activator;
import cc.fooledit.spi.*;
import java.util.function.*;
import java.util.logging.*;
import javafx.scene.effect.*;
import org.osgi.framework.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Activator implements BundleActivator{
	public static final String NAME=Activator.class.getPackage().getName();
	public static final String EFFECT="effect";
	public static final RegistryNode<String,?> REGISTRY=Registry.ROOT.getOrCreateChild(NAME);
	public static final RegistryNode<String,EffectTool> EFFECT_REGISTRY=(RegistryNode<String,EffectTool>)REGISTRY.getOrCreateChild(EFFECT);
	public static void onLoad(){
		addDataObjectType(GraphicsObjectType.INSTANCE);
		addDataEditor(new GraphicsEditor(),GraphicsObject.class);
		DataObjectTypeRegistry.addToolBox(LayerToolBox.INSTANCE,GraphicsEditor.class);
		DataObjectTypeRegistry.addToolBox(DrawToolBox.INSTANCE,GraphicsEditor.class);
		DataObjectTypeRegistry.addToolBox(EffectToolBox.INSTANCE,GraphicsEditor.class);
		DataObjectTypeRegistry.addToolBox(SelectionToolBox.INSTANCE,GraphicsEditor.class);
		CoreModule.TEMPLATE_TYPE_REGISTRY.put(GraphicsTemplate.class.getName(),(obj)->new GraphicsTemplate((String)obj.get("name"),(String)obj.get("description"),(String)obj.get("file"),(String)obj.get("mime")));
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
		EFFECT_REGISTRY.put(name,new SimpleEffectTool(name,()->null,(n)->effect.get()));
	}
	public static void onUnLoad(){
	}
	public static void onInstall(){
		Registry.providesDataObjectType(GraphicsObjectType.class.getName(),NAME);
		Registry.providesDataObjectEditor(GraphicsEditor.class.getName(),NAME);
		Registry.providesTypeToEditor(GraphicsObject.class.getName(),NAME);
		Registry.providesTemplateType(GraphicsTemplate.class.getName(),NAME);
		Registry.providesToolBox(LayerToolBox.class.getName(),NAME);
		Registry.providesToolBox(DrawToolBox.class.getName(),NAME);
		Registry.providesToolBox(EffectToolBox.class.getName(),NAME);
		Registry.providesToolBox(SelectionToolBox.class.getName(),NAME);
		Registry.providesEditorToToolbox(GraphicsEditor.class.getName(),NAME);
		try{
			((ListRegistryNode)CoreModule.TEMPLATE_REGISTRY.getOrCreateChild("children")).put(
					StandardSerializiers.JSON_SERIALIZIER.decode(Helper.readText(Main.INSTANCE.getFile("templates.json",NAME))));
		}catch(Exception ex){
			Logger.getGlobal().log(Level.INFO,null,ex);
		}
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("image/bmp","cc.fooledit.editor.image.GraphicsObjectType");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("image/gif","cc.fooledit.editor.image.GraphicsObjectType");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("image/jpeg","cc.fooledit.editor.image.GraphicsObjectType");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("image/png","cc.fooledit.editor.image.GraphicsObjectType");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("image/tiff","cc.fooledit.editor.image.GraphicsObjectType");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("image/x-pcx","cc.fooledit.editor.image.GraphicsObjectType");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("image/vnd.wap.wbmp","cc.fooledit.editor.image.GraphicsObjectType");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("image/x-portable-pixmap","cc.fooledit.editor.image.GraphicsObjectType");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("image/x-portable-graymap","cc.fooledit.editor.image.GraphicsObjectType");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("image/x-portable-bitmap","cc.fooledit.editor.image.GraphicsObjectType");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("image/x-portable-anymap","cc.fooledit.editor.image.GraphicsObjectType");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("image/x-raw","cc.fooledit.editor.image.GraphicsObjectType");
		MultiRegistryNode.addChildElement("bmp","image/bmp",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("dib","image/bmp",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("gif","image/gif",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("jpeg","image/jpeg",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("jpg","image/jpeg",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("jpe","image/jpeg",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("png","image/png",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("tif","image/tiff",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("tiff","image/tiff",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("pcx","image/x-pcx",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("wbmp","image/x-pcx",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("ppm","image/x-portable-pixmap",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("pgm","image/x-portable-graymap",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("pbm","image/x-portable-bitmap",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("pnm","image/x-portable-anymap",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("raw","image/x-raw",CoreModule.SUFFIX_REGISTRY);
		CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.put("image/x-bmp","image/bmp");
		CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.put("image/x-MS-bmp","image/bmp");
		CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.put("image/pjpeg","image/jpeg");
	}
	@Override
	public void start(BundleContext bc) throws Exception{
		onInstall();
		onLoad();
	}
	@Override
	public void stop(BundleContext bc) throws Exception{
	}
}
