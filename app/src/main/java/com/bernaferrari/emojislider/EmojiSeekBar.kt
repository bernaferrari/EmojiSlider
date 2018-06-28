package com.bernaferrari.emojislider

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build.VERSION
import android.text.SpannableString
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener

class EmojiSeekBar(outsideview: View) : OnFocusChangeListener,
    OnSeekBarChangeListener {
    private val context: Context = outsideview.context
    //    private final C3292d f31029c;
    //    private final bl f31030d;
    //    private final int pixelsize;
    //    private final C2500c<C3026a> f31032f;
    //    private final View f31033g;
    //    private final ViewStub f31034h;
    private val emojiHelper: EmojiHelper
    private var standardColor = -16777216
    private var bool1: Boolean = false
    private var intIdontknow: Int = 0
    private var bool2: Boolean = false

    private var sliderStickerEditor: View = outsideview.findViewById(R.id.slider_sticker_editor)
    private var sliderStickerQuestion: EditText =
        outsideview.findViewById(R.id.slider_sticker_question)
    private var sliderStickerBackgroundButton: ImageView =
        outsideview.findViewById(R.id.slider_sticker_background_button)
    private var sliderStickerSlider: SeekBar = outsideview.findViewById(R.id.slider_sticker_slider)
    private var sliderParticleSystem: View = outsideview.findViewById(R.id.slider_particle_system)

    init {
        this.emojiHelper = EmojiHelper(this.context)
        initializer()
    }//        this.f31029c = c3292d;
    //        this.f31030d = new C7451i(this);
    //        this.f31032f = c2500c;
    //        this.f31032f.m5860a((C2501d) this);
    //        this.f31033g = view.findViewById(R.id.text_overlay_edit_text_container);
    //        this.f31034h = (ViewStub) view.findViewById(R.id.slider_sticker_editor_stub);

    fun m17121a() {
        if (this.bool1) {
            //            this.sliderStickerQuestion.setTextColor(C3395a.m7506a(this.standardColor));
            (this.sliderStickerEditor.background as GradientDrawable).setColor(this.standardColor)
        } else {
            this.sliderStickerQuestion.setTextColor(this.standardColor)
            (this.sliderStickerEditor.background as GradientDrawable).setColor(-1)
        }
        if (this.bool1) {
            //            m17115a(0, C3395a.m7509b(this.standardColor));
            if (this.sliderStickerQuestion.currentTextColor == -1) {
                m17115a(1, -1)
            } else {
                m17114a(1)
            }
        } else {
            m17114a(0)
            m17114a(1)
        }
        //        if (this.bool1) {
        //            this.sliderStickerQuestion.setHintTextColor(C3395a.m7509b(this.standardColor));
        //        } else {
        //            this.sliderStickerQuestion.setHintTextColor(C0835a.m2245b(this.context, R.color.slider_sticker_question_hint));
        //        }
    }

    private fun m17114a(i: Int) {
        //        C0790f.f3079a.mo3287a(((LayerDrawable) this.sliderStickerSlider.getProgressDrawable()).getDrawable(i), null);
    }

    private fun m17115a(i: Int, i2: Int) {
        //        C0790f.m2003a(((LayerDrawable) this.sliderStickerSlider.getProgressDrawable()).getDrawable(i), i2);
    }

    fun mo2010a(i: Int, z: Boolean) {
        if (this.intIdontknow > i) {
            this.sliderStickerQuestion.clearFocus()
            //            C2912a.m6551a(new C2499b(this.f31032f, new C3699z()));
        }
        this.intIdontknow = i
        EmojiSeekBar.r1(this)
    }

    private fun m17116a() {
        var i = 0
        //        if (c5179d == null) {
        this.sliderStickerQuestion.setText("")
        this.bool1 = false
        updateThumb("😍")
        i = -16777216
        //        } else {
        //            int i2;
        //            this.sliderStickerQuestion.setText(c5179d.f20867h);
        //            this.sliderStickerQuestion.setSelection(this.sliderStickerQuestion.getText().length());
        //            EmojiSeekBar.updateThumb(this, c5179d.f20865f);
        //            String str = c5179d.f20864e;
        //            if (str == null) {
        //                i2 = 0;
        //            } else {
        //                i2 = Color.parseColor(str);
        //            }
        //            this.bool1 = -1 != i2;
        //            if (this.bool1) {
        //                str = c5179d.f20864e;
        //                if (str != null) {
        //                    i = Color.parseColor(str);
        //                }
        //                this.standardColor = i;
        //                m17121a();
        //            }
        //            str = c5179d.f20868i;
        //            if (str != null) {
        //                i = Color.parseColor(str);
        //            }
        //        }
        this.standardColor = i
        m17121a()
    }

    fun initializer() {

        //        if (C3801g.f15740a[((C3026a) obj).ordinal()] == 1) {
        //            px pxVar = this.pixelsize;
        //            C5178c c5178c = new C5178c();
        //            c5178c.f20858e = ((al) this.sliderStickerSlider.getThumb()).spannable.toString();
        //            c5178c.f20859f = this.sliderStickerQuestion.getText().toString();
        //            c5178c.f20855b = this.sliderStickerQuestion.getCurrentTextColor();
        //            c5178c.f20854a = this.bool1 ? this.standardColor : -1;
        //            C5179d c5179d = new C5179d(c5178c);
        //            pxVar.e(2);
        //            px.H(pxVar);
        //            Drawable c7850j = new C7850j(pxVar.d);
        //            c7850j.f32869c = true;
        //            c7850j.invalidateSelf();
        //            c7850j.f32871e = c5179d;
        //            c7850j.m18503c();
        val c5862a = C5862a()
        c5862a.f23125d = true
        c5862a.f23130i = 1.5f
        c5862a.f23129h = 0.25f
        c5862a.f23124c = "TextOverlayController"
        //            C5863b c5863b = new C5863b(c5862a);
        //            String str = c5179d.f20865f;
        //            C3591l c3591l = new C3591l();
        //            StringBuilder stringBuilder = new StringBuilder("emoji_slider_");
        //            stringBuilder.append(str);
        //            c3591l.f14893a = stringBuilder.toString();
        //            C3589j c3589j = new C3589j("slider_sticker_bundle_id", Collections.singletonList(c3591l));
        //            c3589j.f14891q = C3596q.SLIDER;
        //            pxVar.a(c3589j.m7835c(), c7850j, c5863b, null);
        //            this.sliderStickerQuestion.removeTextChangedListener(this.f31030d);
        //            m17120d();
        //        }
        //        if (C3801g.f15740a[c3026a.ordinal()] == 1) {
        //            C5179d c5179d2 = ((az) obj3).f15481a;
        //            if ((this.outsideview != null ? 1 : false) == 0) {
        //                this.outsideview = this.f31034h.inflate();
        //                ag.m7437a(this.sliderStickerEditor, new C8395d(this));
        this.sliderStickerQuestion.onFocusChangeListener = this

        m17116a()

        if (VERSION.SDK_INT >= 21) {
            //                    this.sliderStickerQuestion.setTypeface(ac.m7427a());
            this.sliderStickerQuestion.letterSpacing = -0.03f
        } else {
            this.sliderStickerQuestion.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL)
        }
        //                this.sliderStickerQuestion.setOnClickListener(new C3800e(this));
        sliderStickerSlider.setOnSeekBarChangeListener(this)
        updateThumb("😍")
        sliderParticleSystem.background = emojiHelper
        //                this.viewpager.setAdapter(new C3799c(this.context, this));
        //                CirclePageIndicator circlePageIndicator = this.f31039m;
        //                circlePageIndicator.setCurrentPage(0);
        //                circlePageIndicator.f33924a = 5;
        //                circlePageIndicator.requestLayout();
        //                this.viewpager.m16168a(this.f31039m);
//        this.sliderStickerBackgroundButton = this.outsideview.findViewById<View>(R.id.slider_sticker_background_button) as ImageView
        //                this.sliderStickerBackgroundButton.setImageResource(R.drawable.text_bg_off);
        //                C3310i c3310i = new C3310i(this.sliderStickerBackgroundButton);
        //                c3310i.f13961c = new C8396f(this);
        //                c3310i.m7293a();
        //            }
        //            ah.m21964b(false, this.f31033g, this.outsideview, this.sliderStickerBackgroundButton);
        this.sliderStickerQuestion.requestFocus()
        //            m17116a(c5179d2);
        //            if (c5179d2 == null) {
        //                this.bool2 = false;
        //            }
        EmojiSeekBar.m17117b(this)
        EmojiSeekBar.r0(this)
        //            this.sliderStickerQuestion.addTextChangedListener(this.f31030d);
        //            this.f31035i.setAdapter(this.f31037k);
        //            this.pixelsize.e(9);
        //        }
    }

    private fun m17120d() {
        //        if ((this.outsideview != null ? 1 : false) != 0) {
        //            ah.m21963a(false, this.f31033g, this.outsideview, this.f31035i, this.f31036j, this.f31038l, this.f31039m, this.sliderStickerBackgroundButton);
        this.sliderStickerQuestion.clearFocus()
        //        }
    }

    override fun onFocusChange(view: View, z: Boolean) {
        if (z) {
            //            this.f31029c.f13893a.add(this);
            //            ag.m7451b(view);
            return
        }
        //        this.f31029c.f13893a.remove(this);
        m7439a(view)
        m17120d()
    }

    override fun onProgressChanged(seekBar: SeekBar, i: Int, z: Boolean) {
        if (z) {
            this.emojiHelper.onProgressChanged(
                this.sliderStickerEditor.x + this.sliderStickerEditor.paddingLeft.toFloat() + this.sliderStickerSlider.paddingLeft.toFloat() + this.sliderStickerSlider.thumb.bounds.left.toFloat(),
                this.sliderStickerEditor.y + this.sliderStickerEditor.paddingTop.toFloat() + this.sliderStickerSlider.top.toFloat() + this.sliderStickerSlider.thumb.bounds.top.toFloat()
            )
            this.emojiHelper.updateProgress(i.toFloat() / 100.0f)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        this.bool2 = false
        EmojiSeekBar.r0(this)
        this.emojiHelper.progressStarted()
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        this.emojiHelper.onStopTrackingTouch()
    }

    private fun generateThumb(context: Context, string: String, size: Int): al {
        val alVar = al(context, getWidthPixels(context))
        alVar.setSpannable(SpannableString(string))
        alVar.setTextSize(context.resources.getDimensionPixelSize(size).toFloat())
        return alVar
    }

    private fun updateThumb(string: String) {
        sliderStickerSlider.thumb = generateThumb(
            context = this.context,
            string = string,
            size = R.dimen.slider_sticker_slider_handle_size
        )
        emojiHelper.emoji = string
    }

    companion object {

        fun m17117b(EmojiSeekBar: EmojiSeekBar) {
            if (EmojiSeekBar.bool1) {
                //            EmojiSeekBar.sliderStickerBackgroundButton.setImageResource(R.drawable.text_bg_on);
            } else {
                //            EmojiSeekBar.sliderStickerBackgroundButton.setImageResource(R.drawable.text_bg_off);
            }
        }

        fun m7439a(view: View?) {
            if (view != null) {
                (view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                    view.windowToken,
                    0
                )
                return
            }
        }

        fun r0(EmojiSeekBar: EmojiSeekBar) {
            //        if (EmojiSeekBar.bool2) {
            //            m21962a(true, EmojiSeekBar.f31035i, EmojiSeekBar.f31036j);
            //            m21963a(false, EmojiSeekBar.f31038l, EmojiSeekBar.f31039m);
            //            return;
            //        }
            //        m21962a(true, EmojiSeekBar.f31038l, EmojiSeekBar.f31039m);
            //        m21963a(false, EmojiSeekBar.f31035i, EmojiSeekBar.f31036j);
        }

        fun r1(EmojiSeekBar: EmojiSeekBar) {
            EmojiSeekBar.sliderStickerEditor.translationY =
                    ((getHeightPixels(EmojiSeekBar.context) - EmojiSeekBar.intIdontknow - EmojiSeekBar.sliderStickerEditor.height) / 2).toFloat()
            //            EmojiSeekBar.f31038l.setTranslationY((float) (-EmojiSeekBar.intIdontknow));
            //            EmojiSeekBar.f31039m.setTranslationY((float) (-EmojiSeekBar.intIdontknow));
        }

        fun getHeightPixels(context: Context): Int {
            return context.resources.displayMetrics.heightPixels
        }

        fun getWidthPixels(context: Context): Int {
            return context.resources.displayMetrics.widthPixels
        }

        fun m21963a(z: Boolean, vararg viewArr: View) {
            //        for (View view : viewArr) {
            //            if (z) {
            //                ah a = m21961a(view).m21967a(0.0f);
            //                a.f36434b.f6938b = true;
            //                a.f36437e = new ab(view);
            //                a.m21966a();
            //            } else {
            //                view.setVisibility(8);
            //                m21961a(view).m21970b();
            //                view.setAlpha(0.0f);
            //            }
            //        }
        }
    }


    //    public static void m21962a(boolean z, ad adVar, View... viewArr) {
    //        Set hashSet;
    //        if (adVar != null) {
    //            hashSet = new HashSet();
    //            for (Object add : viewArr) {
    //                hashSet.add(add);
    //            }
    //        } else {
    //            hashSet = null;
    //        }
    //        for (View view : viewArr) {
    //            if (z) {
    //                view.setVisibility(0);
    //                ad acVar = adVar != null ? new ac(hashSet, view, adVar) : null;
    //                ah a = m21961a(view).m21967a(1.0f);
    //                a.f36437e = acVar;
    //                a.f36434b.f6938b = true;
    //                a.m21966a();
    //            } else {
    //                view.setVisibility(0);
    //                m21961a(view).m21970b();
    //                view.setAlpha(1.0f);
    //            }
    //        }
    //    }
    //}
}